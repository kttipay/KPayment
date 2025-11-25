package com.kttipay.kpayment

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathData
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val GoogleIcon: ImageVector
    get() {
        if (_Symbol != null) {
            return _Symbol!!
        }
        _Symbol = ImageVector.Builder(
            name = "Symbol",
            defaultWidth = 268.15.dp,
            defaultHeight = 273.88.dp,
            viewportWidth = 268.15f,
            viewportHeight = 273.88f
        ).apply {
            group(
                clipPathData = PathData {
                    moveTo(265.58f, 111.54f)
                    lineTo(136.93f, 111.54f)
                    lineToRelative(0f, 52.65f)
                    lineToRelative(73.92f, 0f)
                    curveToRelative(-1.19f, 7.45f, -3.86f, 14.78f, -7.76f, 21.46f)
                    curveToRelative(-4.48f, 7.66f, -10.01f, 13.49f, -15.68f, 17.93f)
                    curveToRelative(-16.99f, 13.3f, -36.8f, 16.02f, -50.56f, 16.02f)
                    curveToRelative(-34.76f, 0f, -64.45f, -22.94f, -75.95f, -54.12f)
                    curveToRelative(-0.46f, -1.13f, -0.77f, -2.3f, -1.15f, -3.46f)
                    curveToRelative(-2.54f, -7.93f, -3.93f, -16.34f, -3.93f, -25.07f)
                    curveToRelative(0f, -9.09f, 1.5f, -17.79f, 4.24f, -26.01f)
                    curveToRelative(10.81f, -32.41f, 41.18f, -56.62f, 76.81f, -56.62f)
                    curveToRelative(7.17f, 0f, 14.07f, 0.87f, 20.61f, 2.61f)
                    curveToRelative(14.96f, 3.97f, 25.54f, 11.79f, 32.02f, 17.98f)
                    lineToRelative(39.12f, -39.13f)
                    curveToRelative(-23.79f, -22.28f, -54.81f, -35.78f, -91.81f, -35.78f)
                    curveToRelative(-29.58f, -0f, -56.89f, 9.41f, -79.27f, 25.32f)
                    curveToRelative(-18.15f, 12.9f, -33.03f, 30.17f, -43.08f, 50.23f)
                    curveToRelative(-9.34f, 18.6f, -14.46f, 39.21f, -14.46f, 61.37f)
                    curveToRelative(0f, 22.16f, 5.12f, 42.99f, 14.47f, 61.42f)
                    lineToRelative(0f, 0.12f)
                    curveToRelative(9.87f, 19.56f, 24.3f, 36.41f, 41.84f, 49.25f)
                    curveToRelative(15.32f, 11.22f, 42.8f, 26.16f, 80.49f, 26.16f)
                    curveToRelative(21.68f, 0f, 40.89f, -3.99f, 57.83f, -11.47f)
                    curveToRelative(12.22f, -5.4f, 23.05f, -12.44f, 32.86f, -21.48f)
                    curveToRelative(12.96f, -11.95f, 23.1f, -26.74f, 30.03f, -43.75f)
                    curveToRelative(6.93f, -17.01f, 10.63f, -36.25f, 10.63f, -57.1f)
                    curveToRelative(0f, -9.71f, -0.96f, -19.58f, -2.57f, -28.54f)
                    close()
                }
            ) {
                path(
                    fill = Brush.radialGradient(
                        colorStops = arrayOf(
                            0.14f to Color(0xFF1ABD4D),
                            0.25f to Color(0xFF6EC30D),
                            0.31f to Color(0xFF8AC502),
                            0.37f to Color(0xFFA2C600),
                            0.45f to Color(0xFFC8C903),
                            0.54f to Color(0xFFEBCB03),
                            0.62f to Color(0xFFF7CD07),
                            0.7f to Color(0xFFFDCD04),
                            0.77f to Color(0xFFFDCE05),
                            0.86f to Color(0xFFFFCE0A)
                        ),
                        center = Offset(100.9f, 232.36f),
                        radius = 136.51f
                    )
                ) {
                    moveTo(-1.97f, 137.86f)
                    curveToRelative(0.14f, 21.81f, 6.23f, 44.32f, 15.44f, 62.49f)
                    lineToRelative(0f, 0.13f)
                    curveToRelative(6.66f, 13.19f, 15.75f, 23.62f, 26.11f, 33.94f)
                    lineToRelative(62.58f, -23.32f)
                    curveToRelative(-11.84f, -6.14f, -13.65f, -9.91f, -22.13f, -16.77f)
                    curveToRelative(-8.67f, -8.93f, -15.14f, -19.19f, -19.16f, -31.21f)
                    lineToRelative(-0.16f, 0f)
                    lineToRelative(0.16f, -0.13f)
                    curveToRelative(-2.65f, -7.94f, -2.91f, -16.37f, -3.01f, -25.13f)
                    close()
                }
                path(
                    fill = Brush.radialGradient(
                        colorStops = arrayOf(
                            0.41f to Color(0xFFFB4E5A),
                            1f to Color(0xFFFF4540)
                        ),
                        center = Offset(225.86f, 73.01f),
                        radius = 94.63f
                    )
                ) {
                    moveTo(136.93f, -1f)
                    curveToRelative(-6.19f, 22.19f, -3.82f, 43.77f, 0f, 56.32f)
                    curveToRelative(7.14f, 0.01f, 14.02f, 0.88f, 20.55f, 2.61f)
                    curveToRelative(14.96f, 3.97f, 25.53f, 11.79f, 32.02f, 17.98f)
                    lineToRelative(40.12f, -40.13f)
                    curveToRelative(-23.77f, -22.26f, -52.37f, -36.75f, -92.68f, -36.78f)
                    close()
                }
                path(
                    fill = Brush.radialGradient(
                        colorStops = arrayOf(
                            0.23f to Color(0xFFFF4541),
                            0.31f to Color(0xFFFF4540),
                            0.46f to Color(0xFFFF4640),
                            0.54f to Color(0xFFFF473F),
                            0.7f to Color(0xFFFF5138),
                            0.77f to Color(0xFFFF5B33),
                            0.86f to Color(0xFFFF6C29),
                            1f to Color(0xFFFF8C18)
                        ),
                        center = Offset(174.18f, -18.86f),
                        radius = 151.56f
                    )
                ) {
                    moveTo(136.8f, -1.17f)
                    curveToRelative(-30.34f, -0f, -58.35f, 9.65f, -81.3f, 25.97f)
                    curveToRelative(-8.52f, 6.06f, -16.34f, 13.06f, -23.31f, 20.84f)
                    curveToRelative(-1.82f, 17.48f, 13.66f, 38.97f, 44.31f, 38.79f)
                    curveToRelative(14.87f, -17.67f, 36.87f, -29.11f, 61.36f, -29.11f)
                    curveToRelative(0.02f, 0f, 0.04f, 0f, 0.07f, 0f)
                    lineToRelative(-1f, -56.49f)
                    curveToRelative(-0.05f, -0f, -0.09f, -0f, -0.13f, -0f)
                    close()
                }
                path(
                    fill = Brush.radialGradient(
                        colorStops = arrayOf(
                            0.13f to Color(0xFF0CBA65),
                            0.21f to Color(0xFF0BB86D),
                            0.3f to Color(0xFF09B479),
                            0.4f to Color(0xFF08AD93),
                            0.48f to Color(0xFF0AA6A9),
                            0.57f to Color(0xFF0D9CC6),
                            0.67f to Color(0xFF1893DD),
                            0.77f to Color(0xFF258BF1),
                            0.86f to Color(0xFF3086FF)
                        ),
                        center = Offset(138.9f, 257.95f),
                        radius = 395.39f
                    )
                ) {
                    moveToRelative(236.93f, 144.19f)
                    lineToRelative(-27.08f, 19f)
                    curveToRelative(-1.19f, 7.45f, -3.86f, 14.78f, -7.77f, 21.46f)
                    curveToRelative(-4.48f, 7.66f, -10.01f, 13.49f, -15.68f, 17.93f)
                    curveToRelative(-16.96f, 13.27f, -36.72f, 16f, -50.47f, 16.02f)
                    curveToRelative(-14.22f, 24.73f, -16.71f, 37.12f, 1f, 57.08f)
                    curveToRelative(21.91f, -0.02f, 41.34f, -4.06f, 58.48f, -11.62f)
                    curveToRelative(12.39f, -5.47f, 23.36f, -12.6f, 33.3f, -21.77f)
                    curveToRelative(13.13f, -12.11f, 23.41f, -27.1f, 30.43f, -44.34f)
                    curveToRelative(7.02f, -17.24f, 10.77f, -36.73f, 10.77f, -57.87f)
                    close()
                }
                path(fill = SolidColor(Color(0xFF3086FF))) {
                    moveTo(134.93f, 109.54f)
                    lineToRelative(0f, 56.65f)
                    lineToRelative(130.28f, 0f)
                    curveToRelative(1.15f, -7.76f, 4.94f, -17.8f, 4.94f, -26.11f)
                    curveToRelative(0f, -9.71f, -0.95f, -21.58f, -2.57f, -30.54f)
                    close()
                }
                path(
                    fill = Brush.radialGradient(
                        colorStops = arrayOf(
                            0.37f to Color(0xFFFF4E3A),
                            0.46f to Color(0xFFFF8A1B),
                            0.54f to Color(0xFFFFA312),
                            0.62f to Color(0xFFFFB60C),
                            0.77f to Color(0xFFFFCD0A),
                            0.86f to Color(0xFFFECF0A),
                            0.92f to Color(0xFFFECF08),
                            1f to Color(0xFFFDCD01)
                        ),
                        center = Offset(125.18f, 24.7f),
                        radius = 147.65f
                    )
                ) {
                    moveTo(32.81f, 43.64f)
                    curveToRelative(-8.04f, 8.98f, -14.91f, 19.04f, -20.35f, 29.92f)
                    curveToRelative(-9.34f, 18.6f, -14.46f, 41.21f, -14.46f, 63.38f)
                    curveToRelative(0f, 0.31f, 0.03f, 0.62f, 0.03f, 0.93f)
                    curveToRelative(4.14f, 8.1f, 57.16f, 6.55f, 59.83f, 0f)
                    curveToRelative(-0f, -0.31f, -0.04f, -0.6f, -0.04f, -0.91f)
                    curveToRelative(0f, -9.09f, 1.5f, -15.79f, 4.24f, -24.01f)
                    curveToRelative(3.38f, -10.14f, 8.67f, -19.47f, 15.44f, -27.51f)
                    curveToRelative(1.53f, -2f, 5.63f, -6.3f, 6.82f, -8.88f)
                    curveToRelative(0.45f, -0.98f, -0.83f, -1.53f, -0.9f, -1.88f)
                    curveToRelative(-0.08f, -0.39f, -1.8f, -0.08f, -2.18f, -0.36f)
                    curveToRelative(-1.22f, -0.92f, -3.64f, -1.39f, -5.11f, -1.82f)
                    curveToRelative(-3.14f, -0.91f, -8.34f, -2.91f, -11.23f, -4.99f)
                    curveToRelative(-9.13f, -6.56f, -23.39f, -14.4f, -32.09f, -23.86f)
                    close()
                }
                path(
                    fill = Brush.radialGradient(
                        colorStops = arrayOf(
                            0.32f to Color(0xFFFF4C3C),
                            0.6f to Color(0xFFFF692C),
                            0.73f to Color(0xFFFF7825),
                            0.88f to Color(0xFFFF8D1B),
                            1f to Color(0xFFFF9F13)
                        ),
                        center = Offset(101.25f, 23.17f),
                        radius = 73.34f
                    )
                ) {
                    moveTo(65.1f, 74.7f)
                    curveToRelative(21.18f, 13.11f, 27.27f, -6.61f, 41.36f, -12.79f)
                    lineTo(81.96f, 10.03f)
                    curveToRelative(-9.01f, 3.87f, -17.53f, 8.67f, -25.43f, 14.29f)
                    curveToRelative(-11.8f, 8.39f, -22.22f, 18.62f, -30.82f, 30.27f)
                    close()
                }
                path(
                    fill = Brush.radialGradient(
                        colorStops = arrayOf(
                            0.23f to Color(0xFF0FBC5F),
                            0.31f to Color(0xFF0FBC5F),
                            0.37f to Color(0xFF0FBC5E),
                            0.46f to Color(0xFF0FBC5D),
                            0.54f to Color(0xFF12BC58),
                            0.7f to Color(0xFF28BF3C),
                            0.77f to Color(0xFF38C02B),
                            0.86f to Color(0xFF52C218),
                            0.92f to Color(0xFF67C30F),
                            1f to Color(0xFF86C504)
                        ),
                        center = Offset(174.18f, 292.74f),
                        radius = 151.56f
                    )
                ) {
                    moveTo(73.72f, 207.09f)
                    curveToRelative(-28.43f, 10.48f, -32.89f, 10.86f, -35.5f, 28.86f)
                    curveToRelative(5f, 4.99f, 10.38f, 9.6f, 16.09f, 13.78f)
                    curveToRelative(15.32f, 11.22f, 44.8f, 26.16f, 82.49f, 26.16f)
                    curveToRelative(0.04f, 0f, 0.09f, -0f, 0.13f, -0f)
                    lineToRelative(0f, -58.29f)
                    curveToRelative(-0.03f, 0f, -0.06f, 0f, -0.09f, 0f)
                    curveToRelative(-14.12f, 0f, -25.4f, -3.79f, -36.96f, -10.37f)
                    curveToRelative(-2.85f, -1.62f, -8.02f, 2.74f, -10.65f, 0.79f)
                    curveToRelative(-3.63f, -2.69f, -12.36f, 2.32f, -15.5f, -0.92f)
                    close()
                }
                path(
                    fill = Brush.linearGradient(
                        colorStops = arrayOf(
                            0f to Color(0xFF0FBC5C),
                            1f to Color(0xFF0CBA65)
                        ),
                        start = Offset(120.28f, 245.82f),
                        end = Offset(153.59f, 245.82f)
                    ),
                    fillAlpha = 0.5f,
                    strokeAlpha = 0.5f
                ) {
                    moveTo(120.28f, 215.76f)
                    lineToRelative(0f, 59.11f)
                    curveToRelative(5.27f, 0.63f, 10.76f, 1.01f, 16.52f, 1.01f)
                    curveToRelative(5.77f, 0f, 11.36f, -0.3f, 16.78f, -0.86f)
                    lineToRelative(0f, -58.87f)
                    curveToRelative(-6.08f, 1.06f, -11.81f, 1.44f, -16.74f, 1.44f)
                    curveToRelative(-5.68f, 0f, -11.21f, -0.68f, -16.56f, -1.84f)
                    close()
                }
            }
        }.build()

        return _Symbol!!
    }

@Suppress("ObjectPropertyName")
private var _Symbol: ImageVector? = null