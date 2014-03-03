package br.com.tap4mobile.airhockey.util;

import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_COMPILE_STATUS;
import static android.opengl.GLES20.GL_LINK_STATUS;
import static android.opengl.GLES20.GL_VALIDATE_STATUS;

import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glShaderSource;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glGetShaderiv;
import static android.opengl.GLES20.glGetProgramiv;
import static android.opengl.GLES20.glGetShaderInfoLog;
import static android.opengl.GLES20.glGetProgramInfoLog;
import static android.opengl.GLES20.glDeleteShader;
import static android.opengl.GLES20.glDeleteProgram;
import static android.opengl.GLES20.glValidateProgram;


public class ShaderHelper {

	private static final String TAG = "ShaderHelper";

	public static int compileVertexShader(String shaderCode){
		return compileShader(GL_VERTEX_SHADER, shaderCode);
	}

	public static int compileFragmentShader(String shaderCode){
		return compileShader(GL_FRAGMENT_SHADER, shaderCode);
	}

	private static int compileShader (int type, String shaderCode) {

		//Create a Shader object
		final int shaderObjectId = glCreateShader(type);

		//		WrapperLog.log(TAG, shaderCode);

		//Validates if the creation of the obejct was successful
		if (shaderObjectId == 0) {
			WrapperLog.log(TAG, "Could not create new shader.");

			return 0;
		}

		//Associate the shader source code to the created obeject using the shaderObjectId.
		glShaderSource(shaderObjectId, shaderCode);

		//Compile the shader.
		glCompileShader(shaderObjectId);

		//Get the compilation Status of the shader.
		final int[] compileStatus = new int[1];
		glGetShaderiv(shaderObjectId, GL_COMPILE_STATUS, compileStatus, 0);

		//Prints the shader info log to the android log output
		WrapperLog.log(TAG, glGetShaderInfoLog(shaderObjectId));

		//checks if the compilation of the shader was successful
		if (compileStatus[0] == 0) {
			glDeleteShader(shaderObjectId);

			WrapperLog.log(TAG, "compilation of Shader failed.");

			return 0;
		}

		return shaderObjectId;
	}


	public static int linkProgram(int vertexShaderId, int fragmentShaderId){

		//Creating  a new OpenGl Program
		final int programObjectId = glCreateProgram();

		//If zero, there was a failure to create the program.
		if (programObjectId == 0) {
			WrapperLog.log(TAG, "Could not create the new program");

			return 0;
		}

		//Attach the vertex and fragment shader to create the program
		glAttachShader(programObjectId, vertexShaderId);
		glAttachShader(programObjectId, fragmentShaderId);

		//Linking the two shader a attached to the program.
		glLinkProgram(programObjectId);

		//Getting liking status.
		final int[] linkStatus = new int[1];
		glGetProgramiv(programObjectId, GL_LINK_STATUS, linkStatus, 0);

		WrapperLog.log(TAG, "Results of linking program: "+linkStatus[0]+"\n"+glGetProgramInfoLog(programObjectId));

		//Checking if linking was sucessfull. If not, return zero than delete the program object.
		if (linkStatus[0] == 0) {
			glDeleteProgram(programObjectId);
			WrapperLog.log(TAG, "Linking of program failed");

			return 0;
		}

		return programObjectId;
	}

	public static boolean validateProgram(int programObjectId){
		glValidateProgram(programObjectId);

		final int[] validateStatus = new int[1];

		glGetProgramiv(programObjectId, GL_VALIDATE_STATUS, validateStatus, 0);

		WrapperLog.log(TAG, "Results of validating program: "+validateStatus[0]+"\nLog "+glGetProgramInfoLog(programObjectId));

		return validateStatus[0] != 0;
	}

}
