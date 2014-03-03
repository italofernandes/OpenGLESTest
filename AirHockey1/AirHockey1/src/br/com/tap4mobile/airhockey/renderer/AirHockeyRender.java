package br.com.tap4mobile.airhockey.renderer;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glUniformMatrix2fv;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glViewport;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.Matrix.orthoM;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glDrawArrays;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;
import br.com.tap4mobile.airhockey.R;
import br.com.tap4mobile.airhockey.util.ShaderHelper;
import br.com.tap4mobile.airhockey.util.TextResourceReader;
import br.com.tap4mobile.airhockey.util.WrapperLog;

public class AirHockeyRender implements Renderer{

    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int COLOR_COMPONENT_COUNT = 3;

    private static final int BYTES_PER_FLOAT = 4;

    private static final int STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT;

//    private static final String U_COLOR = "u_color";

    private static final String A_COLOR = "a_color";
    private static final String A_POSITION = "a_Position";
    private static final String U_MATRIX = "u_matrix";

    private final float[] projectionMatrix = new float[16];

//    private int uColorLocation;

    private int aPostionLoaction;
    private int aColorLocation;
    private int uMatrixLocation;

    private final FloatBuffer vertexData;
    private Context context;
    private int program;

    public AirHockeyRender(Context context) {

        this.context = context;

        float[] tableVerticesTriangle = {

               /*
               * The first two attributes represent the coodinate to form the triangles.
               * The other three represents the color the triangle area is gonna have.
               * Here openGl will blend these colors with linear interpolation.
               *
               * Order of coordinates: X, Y, R, G, B
               * */

                //Triangle fam Coodenates
                0f, 0f, 1f, 1f, 1f,
                -0.5f, -0.8f, 0.7f, 0.7f, 0.7f,
                0.5f, -0.8f, 0.7f, 0.7f, 0.7f,
                0.5f, 0.8f, 0.7f, 0.7f, 0.7f,
                -0.5f, 0.8f, 0.7f, 0.7f, 0.7f,
                -0.5f, -0.8f, 0.7f, 0.7f, 0.7f,

                // Line 1
                -0.5f, 0f, 1f, 0f, 0f,
                0.5f, 0f, 1f, 0f, 0f,

                // Mallets
                0f, -0.25f, 0f, 0f, 1f,
                0f, 0.25f, 1f, 0f, 0f

        };

        //Allocate native memory to hold our table vertice.
        vertexData = ByteBuffer.allocateDirect(tableVerticesTriangle.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
        //puts tableVerticesTriangle into native memory so OpenGl can access it.
        vertexData.put(tableVerticesTriangle);
    }

    @Override
    public void onDrawFrame(GL10 gl) {

        glClear(GL_COLOR_BUFFER_BIT);

        glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0);

//        glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f);
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6);

//        glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
        glDrawArrays(GL_LINES, 6, 2);

//        glUniform4f(uColorLocation, 0.0f, 0.0f, 1.0f, 1.0f);
        glDrawArrays(GL_POINTS, 8, 1);

//        glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
        glDrawArrays(GL_POINTS, 9, 1);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);

        final float aspectRatio =  width  > height ? (float)width / (float)height : (float)height / (float)width;

        if (width > height){
            orthoM(projectionMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f,-1f, 1f);
        } else {
            orthoM(projectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f);
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        Log.d("Jogo", "onSurfaceCreated");

        //Clears the screen.
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        //Loads Vertex Shader from file.
        String vertexShaderSource = TextResourceReader.readTextFileResource(context, R.raw.simple_vertex_shader);
        //Loads Fragment Shader from file.
        String fragmentShaderSource = TextResourceReader.readTextFileResource(context, R.raw.simple_fragment_shader);

        //Compile Vertex Shader
        int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
        //Compile Fragment Shader
        int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);

        //Links the two Shaders in one program.
        program = ShaderHelper.linkProgram(vertexShader, fragmentShader);

        if (WrapperLog.ON) {
            //Validates the OpenGl Program.
            boolean isProgramOk = ShaderHelper.validateProgram(program);

            //Will Log only if OpenGL program is OK.
            if (isProgramOk) {
                WrapperLog.log("Jogo", "Program is ok to run");
            }
        }

        //Uses the program to render the view.
        glUseProgram(program);

        //Getting the location of a Uniform and an Attribute in an OpenGL program.
//        uColorLocation = glGetUniformLocation(program, U_COLOR);
        aPostionLoaction = glGetAttribLocation(program, A_POSITION);
        aColorLocation = glGetAttribLocation(program, A_COLOR);
        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);

//        WrapperLog.log("Jogo", "uColorLocation "+uColorLocation);
        WrapperLog.log("Jogo", "aPostionLoaction "+aPostionLoaction);

        //Initiates with position 0 so glVertexAttribPointer knows where to the triangle coordinates starts in tableVerticesTriangle.
        vertexData.position(0);
        glVertexAttribPointer(aPostionLoaction, POSITION_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData);
        glEnableVertexAttribArray(aPostionLoaction);

        //Initiates with position POSITION_COMPONENT_COUNT so glVertexAttribPointer knows where color area of the triangle coordinate starts in tableVerticesTriangle.
        vertexData.position(POSITION_COMPONENT_COUNT);
        glVertexAttribPointer(aColorLocation, COLOR_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData);
        glEnableVertexAttribArray(aColorLocation);

        WrapperLog.log("Jogo", "Sucess in initializing render.");
    }
}
