package br.com.tap4mobile.airhockey.renderer;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;
import static android.opengl.GLES20.glUseProgram;
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

public class AirHockeyrender implements Renderer{

	private static final int POSITION_COMPONENT_COUNT = 2;
	private static final int BYTES_PER_FLOAT = 4;

	private static final String U_COLOR = "u_color";
	private static final String A_POSITION = "a_Position";

	private int uColorLocation;
	private int aPostionLoaction;

	private final FloatBuffer vertexData;
	private Context context;
	private int program;

	public AirHockeyrender(Context context) {

		this.context = context;

		float[] tableVerticesTriangle = {

				//Triangle 1
				-0.5f, -0.5f,
				0.5f, 0.5f,
				-0.5f, 0.5f,

				//Triangle 2
				-0.5f, -0.5f,
				0.5f, -0.5f,
				0.5f, 0.5f,

				//Line 1
				-0.5f, 0f,
				0.5f, 0f,

				//Mallets
				0f, -0.25f,
				0f, 0.25f
		};

		vertexData = ByteBuffer.allocateDirect(tableVerticesTriangle.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
		vertexData.put(tableVerticesTriangle);
	}

	@Override
	public void onDrawFrame(GL10 gl) {

		glClear(GL_COLOR_BUFFER_BIT);

		glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f);
		glDrawArrays(GL_TRIANGLES, 0, 6);

		glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
		glDrawArrays(GL_LINES, 6, 2);

		glUniform4f(uColorLocation, 0.0f, 0.0f, 1.0f, 1.0f);
		glDrawArrays(GL_POINTS, 8, 1);

		glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
		glDrawArrays(GL_POINTS, 9, 1);
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		glViewport(0, 0, width, height);
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {

		Log.d("Jogo", "onSurfaceCreated");

		//		glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
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

			if (isProgramOk) {
				WrapperLog.log("Jogo", "Program is ok to run");
			}
		}

		//Uses the program to render the view.
		glUseProgram(program);

		//Getting the location of a Uniform and an Attribute in an OpenGL program.
		uColorLocation = glGetUniformLocation(program, U_COLOR);
		aPostionLoaction = glGetAttribLocation(program, A_POSITION);

		WrapperLog.log("Jogo", "uColorLocation "+uColorLocation);
		WrapperLog.log("Jogo", "aPostionLoaction "+aPostionLoaction);

		vertexData.position(0);

		glVertexAttribPointer(aPostionLoaction, POSITION_COMPONENT_COUNT, GL_FLOAT, false, 0, vertexData);

		glEnableVertexAttribArray(aPostionLoaction);

		WrapperLog.log("Jogo", "Sucess in initializing render.");
	}
}
