package com.sweetljk.vitalassist.cine;

/** 相机旋转调用的临时 roll 作用域，供 mixin 在 setRotation 内消费。 */
public interface CineCameraRotation {

    /** 单位度；NaN 表示当前不在电影视角的 setRotation 调用中，须保留原参数。 */
    float cineRollDegrees();

    default CinePose cineAppliedPose() {
        return null;
    }
}
