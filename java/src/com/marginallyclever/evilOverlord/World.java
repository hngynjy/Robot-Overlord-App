package com.marginallyclever.evilOverlord;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.media.opengl.GL2;
import javax.media.opengl.GLProfile;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import java.io.IOException;


public class World
implements ActionListener {
	/* menus */
	JMenuItem buttonRescan, buttonDisconnect;
	
	/* world contents */
	Camera camera = null;
	
	Arm5Robot robot0 = null;

	LightObject light0,light1;

	public World() {
		camera = new Camera();
		
		light0 = new LightObject();
		light1 = new LightObject();
		
		robot0 = new Arm5Robot();
	}
	

    protected void setup( GL2 gl2 ) {
		gl2.glDepthFunc(GL2.GL_LESS);
		gl2.glEnable(GL2.GL_DEPTH_TEST);
		gl2.glDepthMask(true);
		
		setupLights();
		loadTextures(gl2);
    }
    

    protected void setupLights() {
    	light1.index=1;
	    light1.position=new float[]{-1,-1,1,0};
	    light1.ambient=new float[]{0.0f,0.0f,0.0f,1f};
	    light1.diffuse=new float[]{2.0f,2.0f,2.0f,1f};
	    light1.specular=new float[]{1.0f,1.0f,1.0f,1f};
    }
    
	
	void loadTextures( GL2 gl2 ) {
		// world background texture
		try {
			BufferedImage[] img = new BufferedImage[6];
			img[0] = ImageIO.read(new File("cube-x-pos.png"));
			img[1] = ImageIO.read(new File("cube-y-pos.png"));
			img[2] = ImageIO.read(new File("cube-z-pos.png"));
			img[3] = ImageIO.read(new File("cube-x-neg.png"));
			img[4] = ImageIO.read(new File("cube-y-neg.png"));
			img[5] = ImageIO.read(new File("cube-z-neg.png"));
/*
			Raster r = img[0].getData();
			DataBuffer d = r.getDataBuffer();
			IntBuffer textures = IntBuffer.allocate(6);
			gl2.glGenTextures(6, textures);

			gl2.glBindTexture(GL2.GL_TEXTURE_2D, textures.get(0));
			gl2.glTexImage2D(GL2.GL_TEXTURE_2D, 0,GL2.GL_RGB, 256, 256, GL2.GL_UNSIGNED_BYTE, d );

			gl2.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);
			gl2.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
*/
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	
    public void mouseClicked(MouseEvent e) {}
    public void mouseDragged(MouseEvent e) {
    	camera.mouseDragged(e);
    }
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseMoved(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {
    	camera.mousePressed(e);
    }
    public void mouseReleased(MouseEvent e) {
    	camera.mouseReleased(e);
    }
    public void mouseWheelMoved(MouseEvent e) {}
    
    public void keyPressed(KeyEvent e) {
    	camera.keyPressed(e);
    	robot0.keyPressed(e);
    }
    public void keyReleased(KeyEvent e) {
    	camera.keyReleased(e);
    	robot0.keyReleased(e);
    }
    

	public void actionPerformed(ActionEvent e) {
		Object subject = e.getSource();
		if(subject==buttonRescan) {
			robot0.connection.detectSerialPorts();
			//robot1.connection.DetectSerialPorts();
			//TODO tell RobotTrainer to update all menus
			MainGUI.getSingleton().updateMenu();
			return;
		}
		if(subject==buttonDisconnect) {
			robot0.connection.closePort();
			//robot1.connection.ClosePort();
			MainGUI.getSingleton().updateMenu();
			return;
		}
	}
	
	
    public JMenu updateMenu() {
    	JMenu menu, subMenu;
        
        // connection menu
        menu = new JMenu("Connection(s)");
        menu.setMnemonic(KeyEvent.VK_T);
        menu.getAccessibleContext().setAccessibleDescription("Connection settings.");
        
    	subMenu=robot0.connection.getMenu();
        subMenu.setText("Arm 0");
        menu.add(subMenu);
/*
     	subMenu=robot1.getMenu();
        subMenu.setText("Arm 1");
        menu.add(subMenu);
*/
        buttonRescan = new JMenuItem("Rescan Ports",KeyEvent.VK_R);
        buttonRescan.getAccessibleContext().setAccessibleDescription("Rescan the available ports.");
        buttonRescan.addActionListener(this);
        menu.add(buttonRescan);

        menu.addSeparator();
        
        buttonDisconnect = new JMenuItem("Disconnect",KeyEvent.VK_D);
        buttonDisconnect.addActionListener(this);
        menu.add(buttonDisconnect);
        
        return menu;
    }
    
	
	public void render(GL2 gl2, float dt ) {
		// background color
    	gl2.glClearColor(212.0f/255.0f, 233.0f/255.0f, 255.0f/255.0f, 0.0f);
        // Special handling for the case where the GLJPanel is translucent
        // and wants to be composited with other Java 2D content
        if (GLProfile.isAWTAvailable() &&
            (gl2 instanceof javax.media.opengl.awt.GLJPanel) &&
            !((javax.media.opengl.awt.GLJPanel) gl2).isOpaque() &&
            ((javax.media.opengl.awt.GLJPanel) gl2).shouldPreserveColorBufferIfTranslucent()) {
          gl2.glClear(GL2.GL_DEPTH_BUFFER_BIT);
        } else {
          gl2.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        }
        
        gl2.glDisable(GL2.GL_CULL_FACE);
		//gl2.glEnable(GL2.GL_CULL_FACE);
		//gl2.glCullFace(GL2.GL_BACK);

		
		gl2.glPushMatrix();
			camera.update(dt);
			camera.render(gl2);
			
			 // Enable lighting
			gl2.glShadeModel(GL2.GL_SMOOTH);
			gl2.glEnable(GL2.GL_LIGHTING);
			gl2.glEnable(GL2.GL_COLOR_MATERIAL);
			gl2.glColorMaterial( GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE );
			
			light0.render(gl2);
			light1.render(gl2);
			
		    // draw grid
			gl2.glDisable(GL2.GL_LIGHTING);
			PrimitiveSolids.drawGrid(gl2,50,5);
			gl2.glEnable(GL2.GL_LIGHTING);
			
			// draw robots
			robot0.PrepareMove(dt);
			//if(WillCollide(robot0,robot1) == false) 
			{
				robot0.finalizeMove();
				//robot1.FinalizeMove();
			}
			
			gl2.glPushMatrix();
			robot0.render(gl2);
			gl2.glPopMatrix();

		gl2.glPopMatrix();
	}

	
	boolean WillCollide(Arm5Robot a,Arm5Robot b) {
		// TODO complete me
		//Get the cylinders for each robot
		BoundingVolume [] from = a.GetBoundingVolumes();
		BoundingVolume [] to = b.GetBoundingVolumes();
		// test cylinder/cylinder intersection
		for(int i=0;i<from.length;++i) {
			for(int j=0;j<to.length;++j) {
				if(IntersectionTester.CylinderCylinder((Cylinder)from[i],(Cylinder)to[i])) {
					return true;
				}
			}
		}
		// if there is any hit, return true.
		return false;
	}
}
