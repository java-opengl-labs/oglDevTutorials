/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ogldevtutorials.tut17_ambientLighting;

import com.jogamp.newt.awt.NewtCanvasAWT;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.GLBuffers;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import jglm.Mat4;
import jglm.Quat;
import jglm.Vec3;
import ogldevtutorials.tut17_ambientLighting.glsl.LightingTechnique;
import ogldevtutorials.tut17_ambientLighting.util.DirectionalLight;
import ogldevtutorials.tut17_ambientLighting.util.KeyListener;
import ogldevtutorials.util.PersProjInfo;
import ogldevtutorials.util.Pipeline;
import ogldevtutorials.util.Texture;
import ogldevtutorials.util.ViewData;
import ogldevtutorials.util.ViewPole;
import ogldevtutorials.util.ViewScale;

/**
 *
 * @author elect
 */
public class Viewer implements GLEventListener {

    private GLWindow glWindow;
    private NewtCanvasAWT newtCanvasAWT;
    private int imageWidth;
    private int imageHeight;
    private int[] objects;
    private LightingTechnique lightingTechnique;
    private float scale;
    private Pipeline pipeline;
    private Animator animator;
    private ViewPole viewPole;
    private Texture texture;
    private DirectionalLight directionalLight;

    public Viewer() {

        imageWidth = 1024;
        imageHeight = 768;

        scale = 0f;

        directionalLight = new DirectionalLight(new Vec3(1f, 1f, 1f), 0.5f);

        Vec3 cameraPos = new Vec3(0f, 0f, -3f);
        Quat quat = new Quat(0f, 0f, 0f, 1f);
        viewPole = new ViewPole(new ViewData(cameraPos, quat, 10f), new ViewScale(90f / 250f, 0.2f));

        pipeline = new Pipeline();
        pipeline.worldPos(new Vec3(0f, 0f, 0f));
        pipeline.setViewPole(viewPole);
        PersProjInfo persProjInfo = new PersProjInfo(60f, imageWidth, imageHeight, 1f, 100f);
        pipeline.setPerspectiveProj(persProjInfo);

        initGL();
    }

    private void initGL() {

        GLProfile gLProfile = GLProfile.getDefault();

        GLCapabilities gLCapabilities = new GLCapabilities(gLProfile);

        glWindow = GLWindow.create(gLCapabilities);

        newtCanvasAWT = new NewtCanvasAWT(glWindow);

        glWindow.setSize(imageWidth, imageHeight);

        glWindow.addGLEventListener(this);
        glWindow.addMouseListener(viewPole);
        glWindow.addKeyListener(new KeyListener());

        animator = new Animator(glWindow);
        animator.start();
    }

    @Override
    public void init(GLAutoDrawable glad) {

        GL3 gl3 = glad.getGL().getGL3();

        gl3.glClearColor(0f, 0f, 0f, 0f);
        gl3.glFrontFace(GL3.GL_CCW);
        gl3.glCullFace(GL3.GL_BACK);
        gl3.glEnable(GL3.GL_CULL_FACE);

        objects = new int[Objects.size.ordinal()];
        createVertexBuffer(gl3);
        createIndexBuffer(gl3);

        lightingTechnique = new LightingTechnique(gl3, "/ogldevtutorials/tut17_ambientLighting/glsl/shaders/", 
                "lighting_VS.glsl", "lighting_FS.glsl");

        lightingTechnique.bind(gl3);
        {
            gl3.glUniform1i(lightingTechnique.getgSamplerUL(), 0);
        }
        lightingTechnique.unbind(gl3);

        texture = new Texture(GL3.GL_TEXTURE_2D, "test.png");

        texture.load(gl3);
    }

    private void createVertexBuffer(GL3 gl3) {

        float[] vertices = new float[]{
            -1f, -1f, 0.5773f, 0f, 0f,
            0f, -1f, -1.15475f, 0.5f, 0f,
            1f, -1f, 0.5773f, 1f, 0f,
            0f, 1f, 0f, 0.5f, 1f
        };

        gl3.glGenBuffers(1, objects, Objects.vbo.ordinal());

        gl3.glBindBuffer(GL3.GL_ARRAY_BUFFER, objects[Objects.vbo.ordinal()]);
        {
            FloatBuffer buffer = GLBuffers.newDirectFloatBuffer(vertices);

            gl3.glBufferData(GL3.GL_ARRAY_BUFFER, vertices.length * 4, buffer, GL3.GL_STATIC_DRAW);
        }
        gl3.glBindBuffer(GL3.GL_ARRAY_BUFFER, 0);
    }

    private void createIndexBuffer(GL3 gl3) {

        int[] indices = new int[]{
            0, 3, 1,
            1, 3, 2,
            2, 3, 0,
            1, 2, 0
        };

        gl3.glGenBuffers(1, objects, Objects.ibo.ordinal());

        gl3.glBindBuffer(GL3.GL_ELEMENT_ARRAY_BUFFER, objects[Objects.ibo.ordinal()]);
        {
            IntBuffer buffer = GLBuffers.newDirectIntBuffer(indices);

            gl3.glBufferData(GL3.GL_ELEMENT_ARRAY_BUFFER, indices.length * 4, buffer, GL3.GL_STATIC_DRAW);
        }
        gl3.glBindBuffer(GL3.GL_ELEMENT_ARRAY_BUFFER, 0);
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

        scale += 0.1f;
        lightingTechnique.bind(gl3);
        {
            pipeline.rotate(new Vec3(0f, scale, 0f));

            Mat4 matrix = pipeline.getWVPTrans();

            gl3.glUniformMatrix4fv(lightingTechnique.getgWvpUL(), 1, false, matrix.toFloatArray(), 0);
            
            lightingTechnique.setDirectionalLight(gl3, directionalLight);

            gl3.glEnableVertexAttribArray(0);
            gl3.glEnableVertexAttribArray(1);
            {
                gl3.glBindBuffer(GL3.GL_ARRAY_BUFFER, objects[Objects.vbo.ordinal()]);
                {
                    gl3.glVertexAttribPointer(0, 3, GL3.GL_FLOAT, false, 5 * 4, 0);
                    gl3.glVertexAttribPointer(1, 2, GL3.GL_FLOAT, false, 5 * 4, 3 * 4);

                    gl3.glBindBuffer(GL3.GL_ELEMENT_ARRAY_BUFFER, objects[Objects.ibo.ordinal()]);
                    {
                        texture.bind(gl3, GL3.GL_TEXTURE0);
                        {
                            gl3.glDrawElements(GL3.GL_TRIANGLES, 12, GL3.GL_UNSIGNED_INT, 0);
                        }
                        texture.unbind(gl3, GL3.GL_TEXTURE0);
                    }
                    gl3.glBindBuffer(GL3.GL_ELEMENT_ARRAY_BUFFER, 0);
                }
                gl3.glBindBuffer(GL3.GL_ARRAY_BUFFER, 0);
            }
            gl3.glDisableVertexAttribArray(0);
            gl3.glDisableVertexAttribArray(1);
        }
        lightingTechnique.unbind(gl3);
    }

    @Override
    public void reshape(GLAutoDrawable glad, int i, int i1, int i2, int i3) {
        System.out.println("reshape (" + i + ", " + i1 + ") (" + i2 + ", " + i3 + ")");

        imageWidth = i2;
        imageHeight = i3;

        GL3 gl3 = glad.getGL().getGL3();

        gl3.glViewport(i, i1, i2, i3);
    }

    public NewtCanvasAWT getNewtCanvasAWT() {
        return newtCanvasAWT;
    }

    public GLWindow getGlWindow() {
        return glWindow;
    }

    public Animator getAnimator() {
        return animator;
    }

    public DirectionalLight getDirectionalLight() {
        return directionalLight;
    }

    private enum Objects {

        vbo,
        ibo,
        size
    }
}
