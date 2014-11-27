/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ogldevtutorials.tutorial08;

import com.jogamp.newt.awt.NewtCanvasAWT;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.GLBuffers;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.FloatBuffer;
import javax.media.opengl.GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import jglm.Mat4;
import ogldevtutorials.tutorial08.glsl.Program;

/**
 *
 * @author elect
 */
public class Tutorial08 implements GLEventListener {

    public static void main(String[] args) {

        final Tutorial08 tutorial08 = new Tutorial08();

        final Frame frame = new Frame("Tutorial 08");

        frame.add(tutorial08.getNewtCanvasAWT());

        frame.setSize(tutorial08.getGlWindow().getWidth(), tutorial08.getGlWindow().getHeight());

        frame.setLocation(100, 100);
        
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                tutorial08.getGlWindow().destroy();
                frame.dispose();
                System.exit(0);
            }
        });
        frame.setVisible(true);
    }

    private GLWindow glWindow;
    private NewtCanvasAWT newtCanvasAWT;
    private int imageWidth;
    private int imageHeight;
    private int[] vbo;
    private Program program;
    private float scale;

    public Tutorial08() {

        imageWidth = 1024;
        imageHeight = 768;

        initGL();
    }

    private void initGL() {
        GLProfile gLProfile = GLProfile.getDefault();

        GLCapabilities gLCapabilities = new GLCapabilities(gLProfile);

        glWindow = GLWindow.create(gLCapabilities);

        newtCanvasAWT = new NewtCanvasAWT(glWindow);

        glWindow.setSize(imageWidth, imageHeight);

        glWindow.addGLEventListener(this);

        Animator animator = new Animator(glWindow);
        animator.setRunAsFastAsPossible(true);
        animator.start();
    }

    @Override
    public void init(GLAutoDrawable glad) {
        System.out.println("init");

        GL3 gl3 = glad.getGL().getGL3();

        createVertexBuffer(gl3);

        program = new Program(gl3, "/ogldevtutorials/tutorial08/glsl/shaders/", "VS.glsl", "FS.glsl");

        gl3.glClearColor(0f, 0f, 0f, 0f);

        scale = 0f;
    }

    private void createVertexBuffer(GL3 gl3) {

        float[] vertices = new float[]{
            -1f, -1f, 0f,
            1f, -1f, 0f,
            0f, 1f, 0f
        };

        vbo = new int[1];
        gl3.glGenBuffers(1, vbo, 0);

        gl3.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo[0]);
        {
            FloatBuffer buffer = GLBuffers.newDirectFloatBuffer(vertices);

            gl3.glBufferData(GL3.GL_ARRAY_BUFFER, vertices.length * 4, buffer, GL3.GL_STATIC_DRAW);
        }
        gl3.glBindBuffer(GL3.GL_ARRAY_BUFFER, 0);
    }

    @Override
    public void dispose(GLAutoDrawable glad) {
        System.out.println("dispose");
    }

    @Override
    public void display(GLAutoDrawable glad) {
//        System.out.println("display");

        GL3 gl3 = glad.getGL().getGL3();

        gl3.glClear(GL3.GL_COLOR_BUFFER_BIT);

        scale += 0.001f;

        program.bind(gl3);
        {
            Mat4 gWorld = new Mat4(1f);

            gWorld.c0.x = (float) Math.sin(scale);
            gWorld.c1.y = (float) Math.sin(scale);
            gWorld.c2.z = (float) Math.sin(scale);

            gl3.glUniformMatrix4fv(program.getgWorldUL(), 1, false, gWorld.toFloatArray(), 0);

            gl3.glEnableVertexAttribArray(0);
            {
                gl3.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo[0]);
                {
                    gl3.glVertexAttribPointer(0, 3, GL3.GL_FLOAT, false, 0, 0);

                    gl3.glDrawArrays(GL3.GL_TRIANGLES, 0, 3);
                }
                gl3.glBindBuffer(GL3.GL_ARRAY_BUFFER, 0);
            }
            gl3.glDisableVertexAttribArray(0);
        }
        program.unbind(gl3);
    }

    @Override
    public void reshape(GLAutoDrawable glad, int i, int i1, int i2, int i3) {
        System.out.println("reshape (" + i + ", " + i1 + ") (" + i2 + ", " + i3 + ")");
    }

    public NewtCanvasAWT getNewtCanvasAWT() {
        return newtCanvasAWT;
    }

    public GLWindow getGlWindow() {
        return glWindow;
    }
}
