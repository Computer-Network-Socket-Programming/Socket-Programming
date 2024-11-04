package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Login extends JPanel {

    private static String id;
    private static String pw;
    private final GridBagLayout layout;

    public Login(){
//        super();

        //컴포넌트 생성
        JLabel idLabel = new JLabel("nickname");
        JTextField idField = new JTextField(10);
        JButton btn1 = new JButton("start!");

        //레이아웃 생성, 배치
        layout = new GridBagLayout();
        setLayout(layout);

        gbinsert(idLabel,0,0,1,1);
        gbinsert(idField,3,0,1,1);

        gbinsert(btn1,3,3,1,1);
        setSize(400, 400); //창 크기 설정


        setVisible(true);

        //버튼 눌렀을 때
        btn1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                id = idField.getText();// id 전달
//                pw = new String(pwField.getPassword());
                //System.out.println(id);
                //System.out.println(pw);
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
        new Login();
    }
}
