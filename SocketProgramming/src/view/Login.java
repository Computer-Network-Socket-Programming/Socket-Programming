package view;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Login extends JFrame {
    private static String id;
    private static String pw;
    private GridBagLayout layout;

    public Login(){
        super("login");

        //컴포넌트 생성
        JLabel idLabel = new JLabel("id");
        JLabel pwLabel = new JLabel("pw");
        JTextField idField = new JTextField(10);
        JPasswordField pwField = new JPasswordField(10);
        JButton btn1 = new JButton("로그인");

        //레이아웃 생성, 배치
        layout = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        setLayout(layout);

        gbinsert(idLabel,0,0,1,1);
        gbinsert(idField,3,0,1,1);
        gbinsert(pwLabel,0,2,1,1);
        gbinsert(pwField,3,2,1,1);
        gbinsert(btn1,3,3,1,1);
        setSize(400, 400); //창 크기 설정

        //화면 중앙 배치
        Dimension frameSize = getSize();
        Dimension windowSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((windowSize.width - frameSize.width) / 2,
                (windowSize.height - frameSize.height) / 2);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setVisible(true);

        //버튼 눌렀을 때
        btn1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                id = idField.getText();// id 전달
                pw = new String(pwField.getPassword());
                System.out.println(id);
                System.out.println(pw);
                //new Part1(); //메인 화면 객체 생성하기
                setVisible(false); // 창 안보이게 하기
            }
        });
    }
    public void gbinsert(Component c, int x, int y, int w, int h){
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill= GridBagConstraints.BOTH;
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = w;
        gbc.gridheight = h;
        layout.setConstraints(c,gbc);
        this.add(c);
    }

    public static void main(String[] args) {
        new Login().setVisible(true);
    }
}