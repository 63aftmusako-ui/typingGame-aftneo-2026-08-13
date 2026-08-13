package typingapp.ui;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Rotate;

public class SushiPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    
    private static final int SUSHI_WIDTH = 150;
    private static final int SUSHI_HEIGHT = 150;

    private static final double BASE_HEIGHT = 1080.0;
    
    private static final double BASE_LANE_Y = 900.0;
    
    private int sushiX = -SUSHI_WIDTH;
    private BufferedImage sushiImage;
    
    private final Random random = new Random();
    
    private JFXPanel fxPanel;

    private MeshView sushiMesh;

    private Rotate rotateY;
    private Rotate rotateX;
    private double angle = 0.0;

    public SushiPanel(String difficulty) {
        setOpaque(false); // 背景画像を透過させるため
        setLayout(new BorderLayout());

        loadSushiImage(difficulty);

        initializeJavaFX();
    }

    private void initializeJavaFX() {

        fxPanel = new JFXPanel();

        fxPanel.setOpaque(false);

        add(fxPanel, BorderLayout.CENTER);

        Platform.runLater(() -> {
             
        	// 3D平面を作成

            TriangleMesh mesh = createPlaneMesh();

            sushiMesh = new MeshView(mesh);

            // 表裏どちらからでも見えるようにする

            sushiMesh.setCullFace(CullFace.NONE);
            
            //Y軸回転
            rotateY = new Rotate(0, Rotate.Y_AXIS);
            rotateX = new Rotate(0, Rotate.X_AXIS);

            sushiMesh.getTransforms().addAll(rotateY, rotateX);

            PhongMaterial material = new PhongMaterial();
            Image image = SwingFXUtils.toFXImage(sushiImage, null);
            material.setDiffuseMap(image);
            sushiMesh.setMaterial(material);

            // 3D空間
            Group root = new Group();

            root.getChildren().add(sushiMesh);

            PerspectiveCamera camera = new PerspectiveCamera(false);
            camera.setTranslateZ(-500);

            Scene scene = new Scene(root, getWidth(), getHeight(), true);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
            scene.setCamera(camera);

            fxPanel.setScene(scene);

            updatePosition();
        });
    }

    private TriangleMesh createPlaneMesh() {

        TriangleMesh mesh =
                new TriangleMesh();

        float width = SUSHI_WIDTH;

        float height = SUSHI_HEIGHT;

        float halfWidth =  width / 2.0f;

        float halfHeight = height / 2.0f;

        /*
         * 頂点
         *
         *      0 -------- 1
         *      |          |
         *      |          |
         *      |          |
         *      3 -------- 2
         */
        mesh.getPoints().addAll(
        		-halfWidth, -halfHeight,0,
        		halfWidth, -halfHeight,0,
        		halfWidth,halfHeight,0,
                -halfWidth,halfHeight,0
        );

        /*
         * テクスチャ座標
         */
        mesh.getTexCoords().addAll(
                0, 0,
                1, 0,
                1, 1,
                0, 1
        );

        /*
         * 表面
         */
        mesh.getFaces().addAll(
                0, 0,
                1, 1,
                2, 2,

                0, 0,
                2, 2,
                3, 3
        );

        /*
         * 裏面
         */
        mesh.getFaces().addAll(
                2, 2,
                1, 1,
                0, 0,

                3, 3,
                2, 2,
                0, 0
        );
        return mesh;
    }

    private void updateTexture() {

        if (sushiImage == null || sushiMesh == null) {
            return;
        }

        Image image = SwingFXUtils.toFXImage(sushiImage,null);

        PhongMaterial material = new PhongMaterial();

        material.setDiffuseMap(image);

        sushiMesh.setMaterial(material);
    }
    
    private String getFolderName(String difficulty) {

        switch (difficulty) {

            case "初級": return "寿司緑皿";

            case "中級": return "寿司青皿";

            case "上級": return "寿司赤皿";

            default: throw new IllegalArgumentException("不明な難易度：" + difficulty);
        }
    }
    
    private void loadSushiImage(String difficulty) {

        try {

            String folder = getFolderName(difficulty);

            String fileName = chooseRandomImage(difficulty);

            String path = "/images/sushi/" + folder + "/" + fileName;

            System.out.println("Loading image: " + path);

            var url = getClass().getResource(path);

            if (url == null) {
                throw new RuntimeException("画像がありません : " + path);
            }

            sushiImage = ImageIO.read(url);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private int getLaneY() {

        int panelHeight = getHeight();

        if (panelHeight <= 0) {
            return 0;
        }

        return (int) (BASE_LANE_Y * panelHeight / BASE_HEIGHT);
    }
    
    private List<String> loadSushiList(String difficulty) throws IOException {

        String listFile;

        switch (difficulty) {
            case "初級":
                listFile = "/sushi/寿司緑皿.txt";
                break;

            case "中級":
                listFile = "/sushi/寿司青皿.txt";
                break;

            case "上級":
                listFile = "/sushi/寿司赤皿.txt";
                break;

            default:
                throw new IllegalArgumentException("不明な難易度");
        }

        List<String> sushiList = new ArrayList<>();

        var stream = getClass().getResourceAsStream(listFile);

        if (stream == null) {
            throw new IOException("寿司リストがありません : "  + listFile);
        }
        
        try (BufferedReader reader = new BufferedReader( new InputStreamReader(
                        getClass().getResourceAsStream(listFile),
                        StandardCharsets.UTF_8))) {
            String line;

            while ((line = reader.readLine()) != null) {

                if (!line.isBlank()) {
                    sushiList.add(line.trim());
                }
            }
        }

        return sushiList;
    }
    
    private String chooseRandomImage(String difficulty) throws IOException {

        List<String> sushiList = loadSushiList(difficulty);

        if (sushiList.isEmpty()) {
            throw new RuntimeException("寿司リストが空です");
        }

        String sushiName = sushiList.get(random.nextInt(sushiList.size()));

        return sushiName + ".png";
    }
    
    public void changeSushi(String difficulty) {

        loadSushiImage(difficulty);

        resetPosition();

        repaint();
    }
    
    private void updatePosition() {

        if (sushiMesh == null) {
            return;
        }

        int laneY = getLaneY();
        double x = sushiX + SUSHI_WIDTH / 2.0 - getWidth() / 2.0;
        double y = laneY - SUSHI_HEIGHT / 2.0 - getHeight() / 2.0;

        Platform.runLater(() -> {
        	if (sushiMesh != null) {
        		sushiMesh.setTranslateX(x);
        		sushiMesh.setTranslateY(y);
        		}
        });
    }
    
    @Override
    public void doLayout() {

        super.doLayout();

        if (fxPanel == null) {
            return;
        }
        fxPanel.setBounds( 0, 0, getWidth(), getHeight());
    }
    
    public void move() {
    	
    	int panelWidth = getWidth();
        
        // パネル幅がまだ取得できていない場合の安全対策
        if (panelWidth <= 0) {
            return;
        }
        
        int totalDistance = panelWidth + SUSHI_WIDTH;
        int step = Math.max(2,(int)Math.ceil(totalDistance / 120.0));

        sushiX += step;

        angle += 4.0;
        
        if (angle >= 360.0) {
            angle -= 360.0;
        }
        
        System.out.println("Sushi move: x=" + sushiX + " angle=" + angle);
        /*
         * JavaFXスレッドで回転角度を更新
         */
        double currentAngle = angle;

        Platform.runLater(() -> {
            if (rotateY != null) {
                rotateY.setAngle(currentAngle);
            }
            
            if (rotateX != null) {
            	rotateX.setAngle(currentAngle);
            }
        });
        //画面右端を超えたら左端から再登場
        if (sushiX > panelWidth + SUSHI_WIDTH) {
            resetPosition();
            return;
        }

        /*
         * JavaFX側の位置を更新
         */
        updatePosition();
    }

    public void resetPosition() {
        sushiX = - SUSHI_WIDTH;
        angle = 0.0; // リセット時に角度も0に戻す場合
        Platform.runLater(() -> {
            if (rotateY != null) {rotateY.setAngle(0);}
        });
        
        updatePosition();
        
        repaint();
    }

    public int getSushiPosition() {
    	return sushiX;
    }
}