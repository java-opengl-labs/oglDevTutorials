/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ogldevtutorials.tutorial20.util;

import com.jogamp.newt.event.KeyEvent;
import ogldevtutorials.tutorial20.Tutorial20;

/**
 *
 * @author gbarbieri
 */
public class KeyListener implements com.jogamp.newt.event.KeyListener {

    @Override
    public void keyPressed(KeyEvent ke) {

        float step = 0.05f;

        switch (ke.getKeyCode()) {

            case KeyEvent.VK_A:
                float ambientIntensity = Tutorial20.instance.getDirectionalLight().ambientIntensity;
                ambientIntensity += step;
                Tutorial20.instance.getDirectionalLight().ambientIntensity = ambientIntensity;
                break;

            case KeyEvent.VK_S:
                ambientIntensity = Tutorial20.instance.getDirectionalLight().ambientIntensity;
                ambientIntensity -= step;
                Tutorial20.instance.getDirectionalLight().ambientIntensity = ambientIntensity;
                break;

            case KeyEvent.VK_Z:
                float diffuseIntensity = Tutorial20.instance.getDirectionalLight().ambientIntensity;
                diffuseIntensity += step;
                Tutorial20.instance.getDirectionalLight().ambientIntensity = diffuseIntensity;
                break;

            case KeyEvent.VK_X:
                diffuseIntensity = Tutorial20.instance.getDirectionalLight().ambientIntensity;
                diffuseIntensity -= step;
                Tutorial20.instance.getDirectionalLight().ambientIntensity = diffuseIntensity;
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent ke) {

    }
}
