package demo_exercise;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class demo3 extends JFrame {
	JFrame parent;
	myPane mypane;

	public demo3() {
		super("demo image");
		setSize(800, 600);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		parent = this;
		mypane = new myPane();

		Container c = getContentPane();

		JButton openButton = new JButton("Open");

		openButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				JFileChooser chooser = new JFileChooser("images");
		
				int option = chooser.showOpenDialog(parent);
				if (option == JFileChooser.APPROVE_OPTION) {
					File file = chooser.getSelectedFile();
					loadImage(file);
				} else {
				}
			}
		});

		JButton grayButton = new JButton("gray");

		grayButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				mypane.gray();
			}
		});

		JPanel top = new JPanel();
		top.add(openButton);
		top.add(grayButton);
		c.add(top, "First");
		c.add(new JScrollPane(mypane), "Center");
	}

	private void loadImage(File file) {
		BufferedImage image = null;
		try {
			image = ImageIO.read(file);
		} catch (IOException e) {
			System.out.println("read error: " + e.getMessage());
		}
		mypane.setImage(image);
	}

	public static void main(String args[]) {
		demo3 vc = new demo3();
		vc.setVisible(true);
	}
}

class myPane extends JPanel {
	BufferedImage image;
	Dimension size = new Dimension();

	public myPane() {
	}

	public myPane(BufferedImage image) {
		this.image = image;
		setComponentSize();
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(image, 0, 0, this);
	}

	public Dimension getPreferredSize() {
		return size;
	}

	public void setImage(BufferedImage bi) {
		image = bi;
		setComponentSize();
		repaint();

	}

	public void gray() {
		int W = image.getWidth();
		int H = image.getHeight();
		int r, g, b;
		int rgb;
		int x, y;
		int[][][] A = new int[W][H][3];
		int[][][] B = new int[W][H][3];
		/*
		 * double [][] M= {{1/9.,1/9.,1/9.}, {1/9.,1/9.,1/9.}, {1/9.,1/9.,1/9.}};
		 */
		/*
		 * double [][] M= {{0,0,0}, {0,1,0}, {0,0,0}};
		 */

		double[][] M = { { 1, 2, 1 }, { 0, 0, 0 }, { -1, -2, -1 } };

		/*
		 * double [][] M= {{0,2,0}, {0,0,0}, {0,0,0}};
		 */
//---- read original pixel valuem----
		for (x = 0; x < W; x++)
			for (y = 0; y < H; y++) {
				rgb = image.getRGB(x, y);
				r = (rgb >> 16) & 0xff;
				g = (rgb >> 8) & 0xff;
				b = rgb & 0xff;
				A[x][y][0] = r;
				A[x][y][1] = g;
				A[x][y][2] = b;
				B[x][y][0] = B[x][y][1] = B[x][y][2] = 0;
			}
//--- image processing ---- 
		int i, j, k;
		int s;
		/*
		 * for(x=1;x<W-1;x++) for(y=1;y<H-1;y++) { for(k=0;k<3;k++) { for(i=0;i<3;i++)
		 * for(j=0;j<3;j++) B[x][y][k]+=A[x+i-1][y+j-1][k]*M[i][j];
		 * if(B[x][y][k]>255)B[x][y][k]=255; if(B[x][y][k]<0)B[x][y][k]=0; } }
		 */
		for (x = 1; x < W - 1; x++)
			for (y = 1; y < H - 1; y++) {
				int m, n;
				m = (int) (W * Math.sin(x * 3.1416 * 2 / W));
				n = (int) (H * Math.cos(y * 3.1416 * 2 / H));
				if (m < 0)
					m = 0;
				if (n < 0)
					n = 0;
				if (m > W)
					m = W;
				if (n > H)
					n = H;
				for (k = 0; k < 3; k++)
					B[x][y][k] = A[m][n][k];
			}

//------- set result to image -------

		for (x = 0; x < W; x++)
			for (y = 0; y < H; y++) {
				/*
				 * r=-B[x][y][0]+A[x][y][0]; g=-B[x][y][1]+A[x][y][1]; b=-B[x][y][2]+A[x][y][2];
				 */
				r = B[x][y][0];
				g = B[x][y][1];
				b = B[x][y][2];
				if (r < 0)
					r = 0;
				if (g < 0)
					g = 0;
				if (b < 0)
					b = 0;
				if (r > 255)
					r = 255;
				if (g > 255)
					g = 255;
				if (b > 255)
					b = 255;
				rgb = (r & 0xff) << 16 | (g & 0xff) << 8 | (b & 0xff);
				image.setRGB(x, y, rgb);
			}

		/*
		 * //r=255-r; //g=255-g; //b=255-b;
		 * 
		 * //r=g=b=(r+g+b)/3;
		 * 
		 * int gray=(r+g+b)/3; if(gray>200) r=g=b=255; else r=g=b=0;
		 * 
		 * 
		 * rgb=(r &0xff)<<16 | (g &0xff)<<8 | (b &0xff); image.setRGB(x, y, rgb); }
		 */
		setComponentSize();
		repaint();
	}

	private void setComponentSize() {
		if (image != null) {
			size.width = image.getWidth();
			size.height = image.getHeight();
			revalidate();
		}
	}

}