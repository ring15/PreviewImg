package com.founq.sdk.previewimg;

import android.animation.TypeEvaluator;

import com.founq.sdk.previewimg.entity.MyMatrix;

/**
 * Created by ring on 2019/8/16.
 */
public class MatrixEvaluator implements TypeEvaluator<MyMatrix> {
    @Override
    public MyMatrix evaluate(float v, MyMatrix myMatrix, MyMatrix t1) {
        float[] values = new float[9];
        float[] startValue = myMatrix.getValue();
        float[] endValue = t1.getValue();
        values[MyMatrix.MSCALE_X] = startValue[MyMatrix.MSCALE_X] + v * (endValue[MyMatrix.MSCALE_X] - startValue[MyMatrix.MSCALE_X]);
        values[MyMatrix.MSKEW_X] = startValue[MyMatrix.MSKEW_X] + v * (endValue[MyMatrix.MSKEW_X] - startValue[MyMatrix.MSKEW_X]);
        values[MyMatrix.MTRANS_X] = startValue[MyMatrix.MTRANS_X] + v * (endValue[MyMatrix.MTRANS_X] - startValue[MyMatrix.MTRANS_X]);
        values[MyMatrix.MSKEW_Y] = startValue[MyMatrix.MSKEW_Y] + v * (endValue[MyMatrix.MSKEW_Y] - startValue[MyMatrix.MSKEW_Y]);
        values[MyMatrix.MSCALE_Y] = startValue[MyMatrix.MSCALE_Y] + v * (endValue[MyMatrix.MSCALE_Y] - startValue[MyMatrix.MSCALE_Y]);
        values[MyMatrix.MTRANS_Y] = startValue[MyMatrix.MTRANS_Y] + v * (endValue[MyMatrix.MTRANS_Y] - startValue[MyMatrix.MTRANS_Y]);
        values[MyMatrix.MPERSP_0] = startValue[MyMatrix.MPERSP_0] + v * (endValue[MyMatrix.MPERSP_0] - startValue[MyMatrix.MPERSP_0]);
        values[MyMatrix.MPERSP_1] = startValue[MyMatrix.MPERSP_1] + v * (endValue[MyMatrix.MPERSP_1] - startValue[MyMatrix.MPERSP_1]);
        values[MyMatrix.MPERSP_2] = startValue[MyMatrix.MPERSP_2] + v * (endValue[MyMatrix.MPERSP_2] - startValue[MyMatrix.MPERSP_2]);
        return new MyMatrix(values);
    }
}
