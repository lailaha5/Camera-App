// Camera.java
package cameraapp;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.imgcodecs.Imgcodecs;

public class Camera {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private VideoCapture camera;

    public Camera() {
        this.camera = new VideoCapture(0); // الكاميرا الافتراضية
    }

    /**
     * تحاول التقاط صورة وحفظها
     * @param filename اسم الملف المراد حفظه
     * @return true إذا نجح، false إذا فشل
     */
    public boolean capturePhoto(String filename) {
        if (!camera.isOpened()) {
            System.out.println("الكاميرا غير متاحة");
            return false;
        }

        Mat frame = new Mat();
        if (camera.read(frame)) {
            boolean saved = Imgcodecs.imwrite(filename, frame);
            if (saved) {
                System.out.println("تم حفظ الصورة: " + filename);
            } else {
                System.out.println("فشل في حفظ الصورة");
            }
            camera.release(); // أغلق الكاميرا بعد التقاط الصورة
            return saved;
        } else {
            System.out.println("فشل في قراءة الإطار من الكاميرا");
            camera.release();
            return false;
        }
    }
}
// PhotoCapture.java
package cameraapp;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PhotoCapture {
    private Camera camera;

    public PhotoCapture() {
        this.camera = new Camera();
    }

    /**
     * التقاط صورة وحفظها باسم تلقائي
     * @return اسم الملف إذا نجح، null إذا فشل
     */
    public String takePhoto() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        String filename = "photo_" + dtf.format(LocalDateTime.now()) + ".jpg";
        boolean success = camera.capturePhoto(filename);
        return success ? filename : null;
    }
}
// UserInterface.java
package cameraapp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class UserInterface {
    private JFrame frame;
    private PhotoCapture photoCapture;

    public UserInterface() {
        photoCapture = new PhotoCapture();
        createAndShowGUI();
    }

    private void createAndShowGUI() {
        frame = new JFrame("التقاط صورة");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 150);
        frame.setLayout(new FlowLayout());

        JLabel label = new JLabel("اضغط على الرابط لالتقاط صورة");
        JLabel link = new JLabel("<html><a href='#'>📸 اضغط هنا لالتقاط صورة</a></html>");
        link.setForeground(Color.BLUE);
        link.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // عند الضغط على "الرابط"
        link.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String filename = photoCapture.takePhoto();
                if (filename != null) {
                    JOptionPane.showMessageDialog(frame, "تم التقاط الصورة وحفظها باسم:\n" + filename);
                } else {
                    JOptionPane.showMessageDialog(frame, "فشل التقاط الصورة.\nتأكد أن الكاميرا متاحة.", "خطأ", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        frame.add(label);
        frame.add(link);
        frame.setVisible(true);
    }
}
// Main.java
package cameraapp;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // تشغيل واجهة المستخدم
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new UserInterface();
            }
        });
    }
}
