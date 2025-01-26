package jp.trap.conqest.util

import kotlin.math.abs
import kotlin.math.hypot

fun calcDistanceToLineSegment(
    p: Pair<Double, Double>, a: Pair<Double, Double>, b: Pair<Double, Double>
): Double {
    val ab = b.first - a.first to b.second - a.second
    val ap = p.first - a.first to p.second - a.second
    val bp = p.first - b.first to p.second - b.second
    if (ab.first * ap.first + ab.second * ap.second <= 0) {
        return hypot(ap.first, ap.second)
    }
    if (-ab.first * bp.first + -ab.second * bp.second <= 0) {
        return hypot(bp.first, bp.second)
    }
    return abs(ab.first * ap.second - ab.second * ap.first) / hypot(ab.first, ab.second)
}
