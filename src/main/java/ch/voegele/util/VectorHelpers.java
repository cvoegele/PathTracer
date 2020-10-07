package ch.voegele.util;

import javafx.scene.paint.Color;

public class VectorHelpers {

    private final double GAMMA;

    public VectorHelpers(double GAMMA) {
        this.GAMMA = GAMMA;
    }

    public Vec3 lerp(Vec3 v0, Vec3 v1, double t) {

        //Interpolate
        double x, y, z;

        x = (1 - t) * v0.x + t * v1.x;
        y = (1 - t) * v0.y + t * v1.y;
        z = (1 - t) * v0.z + t * v1.z;

        return new Vec3(x, y, z);
    }

    public Vec3 sRGBtoRGB(Vec3 sRGB) {
        double R1, G1, B1;

        R1 =  Math.pow(sRGB.x / 255d, GAMMA);
        G1 =  Math.pow(sRGB.y / 255d, GAMMA);
        B1 =  Math.pow(sRGB.z / 255d, GAMMA);

        return new Vec3(R1, G1, B1);
    }

    public Vec3 RGBto_sRGB(Vec3 RGB) {
        double sR, sG, sB;

        sR = Math.pow(RGB.x, 1 / GAMMA) * 255;
        sG = Math.pow(RGB.y, 1 / GAMMA) * 255;
        sB = Math.pow(RGB.z, 1 / GAMMA) * 255;

        return new Vec3(sR, sG, sB);
    }

    public Color sRGB255to_Color(Vec3 sRGB255){
        Color sRGB01 = Color.color(sRGB255.x/255, sRGB255.y/255, sRGB255.z/255);
        return sRGB01;
    }

    public int sRGBtoArgb(Vec3 sRGB) {
        int argb = (int) sRGB.x + ((int) sRGB.y << 8) + ((int) sRGB.z << 16);
        return argb;
    }

    public double clamp(double a) {
        if (a >= 1) return 1;
        return a;
    }

    public Vec3 clampsRGB(Vec3 sRGB) {
        Vec3 result = new Vec3(clamp(sRGB.x), clamp(sRGB.y), clamp(sRGB.z));
        return result;
    }
}
