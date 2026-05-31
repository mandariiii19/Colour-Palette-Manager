package adapter;
import model.ColorModel;
public class ColorAdapter {
    public int[] hexToRgb(String hex) {
        String h = hex.startsWith("#") ? hex.substring(1) : hex;
        return new int[]{
                Integer.parseInt(h.substring(0, 2), 16),
                Integer.parseInt(h.substring(2, 4), 16),
                Integer.parseInt(h.substring(4, 6), 16)
        };
    }
    public String rgbToHex(int r, int g, int b) {
        return String.format("#%02X%02X%02X",
                clamp(r), clamp(g), clamp(b));
    }

    /** [255, 107, 107] → [0.0°, 100.0%, 71.0%] */
    public float[] rgbToHsl(int r, int g, int b) {
        float rf = r / 255f, gf = g / 255f, bf = b / 255f;
        float max = Math.max(rf, Math.max(gf, bf));
        float min = Math.min(rf, Math.min(gf, bf));
        float l   = (max + min) / 2f;
        float h   = 0f, s = 0f;

        if (max != min) {
            float d = max - min;
            s = (l > 0.5f) ? d / (2f - max - min) : d / (max + min);

            if      (max == rf) h = (gf - bf) / d + (gf < bf ? 6f : 0f);
            else if (max == gf) h = (bf - rf) / d + 2f;
            else                h = (rf - gf) / d + 4f;
            h /= 6f;
        }
        return new float[]{ h * 360f, s * 100f, l * 100f };
    }


    /** [0.0°, 100.0%, 71.0%] → [255, 107, 107] */
    public int[] hslToRgb(float h, float s, float l) {
        float sf = s / 100f, lf = l / 100f, hf = h / 360f;
        float r, g, b;

        if (sf == 0f) {
            r = g = b = lf;
        } else {
            float q = (lf < 0.5f) ? lf * (1f + sf) : lf + sf - lf * sf;
            float p = 2f * lf - q;
            r = hueToRgb(p, q, hf + 1f / 3f);
            g = hueToRgb(p, q, hf);
            b = hueToRgb(p, q, hf - 1f / 3f);
        }
        return new int[]{ Math.round(r * 255f),
                Math.round(g * 255f),
                Math.round(b * 255f) };
    }


    public ColorModel hexToColorModel(String hex, String label) {
        int[] rgb = hexToRgb(hex);
        ColorModel c = new ColorModel();
        c.setHexValue(hex.toUpperCase().startsWith("#")
                ? hex.toUpperCase()
                : "#" + hex.toUpperCase());
        c.setRgb(rgb[0], rgb[1], rgb[2]);
        c.setLabel((label == null || label.isBlank()) ? hex : label);
        return c;
    }

    public String toRgbString(int r, int g, int b) {
        return String.format("RGB(%d, %d, %d)", r, g, b);
    }

    public String toHslString(String hex) {
        int[]   rgb = hexToRgb(hex);
        float[] hsl = rgbToHsl(rgb[0], rgb[1], rgb[2]);
        return String.format("HSL(%.0f°, %.0f%%, %.0f%%)", hsl[0], hsl[1], hsl[2]);
    }


    private float hueToRgb(float p, float q, float t) {
        if (t < 0f) t += 1f;
        if (t > 1f) t -= 1f;
        if (t < 1f / 6f) return p + (q - p) * 6f * t;
        if (t < 1f / 2f) return q;
        if (t < 2f / 3f) return p + (q - p) * (2f / 3f - t) * 6f;
        return p;
    }

    private int clamp(int v) { return Math.max(0, Math.min(255, v)); }
}
