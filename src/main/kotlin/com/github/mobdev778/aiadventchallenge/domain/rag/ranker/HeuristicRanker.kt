package com.github.mobdev778.aiadventchallenge.domain.rag.ranker

@Suppress("MagicNumber")
class HeuristicRanker : Ranker {

    val sourceHashes = HashSet<Int>()

    override suspend fun init(query: String) {
        val sequences = ArrayList<Seq>()
        for (c in query) {
            val code = c.code
            sequences.add(Seq())
            for (s in sequences) {
                s.hash = s.hash * 31 + code
                sourceHashes.add(s.hash)
            }
        }
    }

    override suspend fun rank(found: String, vector: FloatArray): Double {
        var same = 0

        val sequences = ArrayList<Seq>()
        for (c in found) {
            val code = c.code
            sequences.add(Seq())
            for (s in sequences) {
                s.hash = s.hash * 31 + code
                if (sourceHashes.contains(s.hash)) {
                    same++
                } else {
                    s.alive = false
                }
            }
        }
        for (i in sequences.size - 1 downTo 0) {
            if (!sequences[i].alive) {
                sequences.removeAt(i)
            }
        }
        return same.toDouble() / sourceHashes.size
    }
}

private class Seq(
    var hash: Int = 0,
    var alive: Boolean = true
)
