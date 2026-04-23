# Main Entry Point (Python Layer)
# Manages the full state of an inference request.
# - I/O: Receives raw input from Java bridge (JSON/byte stream with user sample + candidate vectors)
# - Weights: passes cluster multipliers (e.g., Logic Rigidity x1.5, Tone Stability x0.5) to cosine_sim.py
# - Control: Calls cosine_sim.py for core vector similarity (matrix computation)
# - Post: Sorts results, filters previously seen items (avoids repeat/stall), selects Top 10
# - Output: Packages and returns final IDs to Java layer

import json
import os
import sys

sys.path.insert(0, os.path.join(os.path.dirname(__file__), "math"))
from cosine_sim import weighted_cosine

VECTOR_LENGTH = 10
TOP_N = 10


def main():
    try:
        payload     = json.loads(sys.stdin.read())
        user_vector = payload["user_vector"]
        weights     = payload["weights"]
        candidates  = payload["candidates"]
        constraints = payload["constraints"]
        seen_ids    = set(payload.get("seen_ids", []))
    except (json.JSONDecodeError, KeyError) as e:
        print(json.dumps({"error": f"invalid input: {e}"}), file=sys.stderr)
        sys.exit(1)

    if len(user_vector) != VECTOR_LENGTH or len(weights) != VECTOR_LENGTH:
        print(json.dumps({"error": f"user_vector and weights must be length {VECTOR_LENGTH}"}), file=sys.stderr)
        sys.exit(1)

    # exclude candidates whose allele value falls inside a hate-zone bound
    def in_hate_zone(vec):
        for c in constraints:
            if c["lower"] <= vec[c["allele"]] <= c["upper"]:
                return True
        return False

    survivors = [
        c for c in candidates
        if c["id"] not in seen_ids and not in_hate_zone(c["vector"])
    ]

    # score survivors
    scored = []
    for c in survivors:
        try:
            score = weighted_cosine(user_vector, c["vector"], weights)
            scored.append({"id": c["id"], "score": score})
        except ValueError:
            continue

    scored.sort(key=lambda x: x["score"], reverse=True)
    print(json.dumps({"results": scored[:TOP_N]}))
    sys.exit(0)


if __name__ == "__main__":
    main()
