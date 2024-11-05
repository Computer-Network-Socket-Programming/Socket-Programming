package view.ohsung;

import controller.ohsung.NaverConnector;
import model.ohsung.EmailDataRepository;
import model.ohsung.NaverUserInfoDTO;
import view.AccConnectView;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

public class MainView {
    private String nickname;
    private JPanel detailPanel;
    private JLabel senderLabel;
    private JLabel subjectLabel;
    private JLabel timeLabel;
    private JTextArea messageContent;
    private JPanel loadingPanel;
    private NaverUserInfoDTO naverUserInfoDTO = NaverUserInfoDTO.getInstance();
    private NaverConnector naverConnector;

    public MainView(String nickname) {
        this.nickname = nickname;
//        this.naverUserInfoDTO = new NaverUserInfoDTO();
    }

    public void createMainFrame() {
        JFrame mainFrame = new JFrame("환영합니다 " + nickname + "님!!");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mainFrame.setSize(1200, 700);
        mainFrame.setLayout(new BorderLayout());

        JPanel topPanel = createTopPanel();
        mainFrame.add(topPanel, BorderLayout.NORTH);

        checkUserInfo(mainFrame);

        mainFrame.setVisible(true);
    }

    private void checkUserInfo(JFrame mainFrame){
        if(naverUserInfoDTO.getUsername() == null || naverUserInfoDTO.getPassword() == null){
            JLabel promptLabel = new JLabel("이메일 인증을 완료해주세요");
            mainFrame.add(promptLabel, BorderLayout.CENTER);
        } else{
            showLoadingPanel(mainFrame);
            loadNaverEmailsInBackground(mainFrame);
        }
    }

    private void showLoadingPanel(JFrame mainFrame){
        loadingPanel = new JPanel();
        JLabel loadingLabel = new JLabel("메일을 불러오는 중입니다...");
        loadingPanel.add(loadingLabel);
        mainFrame.add(loadingPanel, BorderLayout.CENTER);

        mainFrame.revalidate();
        mainFrame.repaint();
    }

    private void loadNaverEmailsInBackground(JFrame mainFrame) {
        // 백그라운드에서 메일을 로드하는 작업 실행
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                getNaverEmails();
                return null;
            }

            @Override
            protected void done() {
                // 로딩 패널 제거 및 메일 목록 UI 표시
                mainFrame.remove(loadingPanel);
                JSplitPane splitPane = createSplitPane();
                mainFrame.add(splitPane, BorderLayout.CENTER);

                mainFrame.revalidate();
                mainFrame.repaint();
            }
        };
        worker.execute();
    }

    private void getNaverEmails(){
        try{
            String username = naverUserInfoDTO.getUsername();
            String password = naverUserInfoDTO.getPassword();

            NaverConnector naverConnector = new NaverConnector(username, password);
            naverConnector.fetchMails();
            naverConnector.disconnect();

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private JPanel createTopPanel(){
        JPanel topPanel = new JPanel(new BorderLayout());

        JButton sendMailButton = new JButton("메일 보내기");
        JButton verifyEmailButton = new JButton("이메일 인증하기");

        topPanel.add(sendMailButton, BorderLayout.WEST);
        topPanel.add(verifyEmailButton, BorderLayout.EAST);

        createSendMailButtonEvent(sendMailButton);
        createVerifyEmailButtonEvent(verifyEmailButton);

        return topPanel;
    }

    //메일 보내기 버튼 이벤트 생성 함수
    private void createSendMailButtonEvent(JButton sendMailButton){
        sendMailButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //메일 보내기 프레임 생성 함수
                showSendMailPopup();
            }
        });
    }

    //여기에 메일 보내기 FRAME 집어 넣으면 됨(상혁 파트)
    private void showSendMailPopup(){

    }

    //이메일 인증 버튼 이벤트 함수
    private void createVerifyEmailButtonEvent(JButton verifyEmailButton){
        verifyEmailButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //이메일 인증 프레임 생성 함수
                showVerifyEmailPopup();
            }
        });
    }

    //이메일 인증 프레임 집어 넣으면 됨(지원님 파트)
    private void showVerifyEmailPopup(){
        AccConnectView accConnectView = new AccConnectView(nickname);
        accConnectView.createAccConnectView();
//        naverUserInfoDTO.setUsername("99doldol@naver.com");
//        naverUserInfoDTO.setPassword("@rnjsdhtjd99");


    }


    private JPanel createCategoryPanel(DefaultListModel<String> folderModel, JList<String[]> mailList, JPanel cardPanel, CardLayout cardLayout) {
        JList<String> folderList = new JList<>(folderModel);
        folderList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        folderList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    String selectedFolder = folderList.getSelectedValue();
                    updateMailList(mailList, selectedFolder);
                }
            }
        });

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("메일 폴더", JLabel.LEFT), BorderLayout.NORTH);
        panel.add(new JScrollPane(folderList), BorderLayout.CENTER);

        return panel;
    }

    private void updateInBoxNaver(List<String[]> mails){
        for (String[] mail : EmailDataRepository.getInstance().getMailData()) {
            mails.add(new String[]{mail[0], mail[1], mail[2], mail[3]});
        }
    }

    private void updateMailList(JList<String[]> mailList, String folderName) {
        DefaultListModel<String[]> listModel = new DefaultListModel<>();

        List<String[]> mails = new ArrayList<>();
        switch (folderName) {
            case "받은메일함":
                updateInBoxNaver(mails);
                break;
            case "보낸메일함":
                mails.add(new String[]{"Charlie", "보낸 메일 제목 1", "2024-11-01 12:00"});
                mails.add(new String[]{"David", "보낸 메일 제목 2", "2024-11-01 13:00"});
                break;
            case "임시보관함":
                mails.add(new String[]{"Eve", "임시 메일 제목 1", "2024-11-01 14:00"});
                break;
            case "내게쓴메일함":
                mails.add(new String[]{"Frank", "내게 쓴 메일 제목", "2024-11-01 16:00"});
                break;
            case "휴지통":
                mails.add(new String[]{"Grace", "삭제된 메일 제목", "2024-11-01 18:00"});
                break;
        }

        for (String[] mail : mails) {
            listModel.addElement(mail);
        }

        mailList.setModel(listModel);
    }

    private JList<String[]> createMailList(JPanel cardPanel, CardLayout cardLayout) {
        DefaultListModel<String[]> listModel = new DefaultListModel<>();
        JList<String[]> mailList = new JList<>(listModel);

        // 커스텀 렌더러 설정
        mailList.setCellRenderer(new ListCellRenderer<String[]>() {
            @Override
            public Component getListCellRendererComponent(JList<? extends String[]> list, String[] value, int index, boolean isSelected, boolean cellHasFocus) {
                JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
                panel.setPreferredSize(new Dimension(500, 60));

                JLabel senderLabel = new JLabel("보낸 사람: " + value[0]);
                JLabel subjectLabel = new JLabel("제목: " + value[1]);
                JLabel timeLabel = new JLabel("시간: " + value[2]);

                panel.add(senderLabel);
                panel.add(subjectLabel);
                panel.add(timeLabel);

                if (isSelected) {
                    panel.setBackground(Color.LIGHT_GRAY);
                } else {
                    panel.setBackground(list.getBackground());
                }

                return panel;
            }
        });

        mailList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // 항목 선택 시 상세 정보 표시
        mailList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int index = mailList.getSelectedIndex();
                    if (index >= 0) {
                        String[] value = mailList.getModel().getElementAt(index);
                        updateDetailPanel(value[0], value[1], value[2], value[3]);
                        cardLayout.show(cardPanel, "detailPanel");
                    }
                }
            }
        });

        return mailList;
    }

    private JPanel createDetailPanel(CardLayout cardLayout, JPanel cardPanel) {
        detailPanel = new JPanel(new BorderLayout());

        senderLabel = new JLabel();
        subjectLabel = new JLabel();
        timeLabel = new JLabel();
        messageContent = new JTextArea("메일 내용 표시...");
        messageContent.setLineWrap(true);
        messageContent.setWrapStyleWord(true);

        detailPanel.add(senderLabel, BorderLayout.NORTH);
        detailPanel.add(subjectLabel, BorderLayout.CENTER);
        detailPanel.add(timeLabel, BorderLayout.SOUTH);
        detailPanel.add(new JScrollPane(messageContent), BorderLayout.CENTER);

        // 뒤로 가기 버튼
        JButton backButton = new JButton("뒤로");
        backButton.addActionListener(e -> cardLayout.show(cardPanel, "infoPanel"));
        detailPanel.add(backButton, BorderLayout.SOUTH);

        return detailPanel;
    }

    private void updateDetailPanel(String sender, String subject, String time, String text) {
        senderLabel.setText("보낸 사람: " + sender);
        subjectLabel.setText("제목: " + subject);
        timeLabel.setText("시간: " + time);
        messageContent.setText("메일 내용 표시..." + text);  // 실제 메일 내용을 여기에 표시할 수 있습니다.
    }

    private JSplitPane createSplitPane() {
        DefaultListModel<String> folderModel = new DefaultListModel<>();
        folderModel.addElement("받은메일함");
        folderModel.addElement("보낸메일함");
        folderModel.addElement("임시보관함");
        folderModel.addElement("내게쓴메일함");
        folderModel.addElement("휴지통");

        CardLayout cardLayout = new CardLayout();
        JPanel cardPanel = new JPanel(cardLayout);
        JList<String[]> mailList = createMailList(cardPanel, cardLayout);

        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.add(new JScrollPane(mailList), BorderLayout.CENTER);

        JPanel categoryPanel = createCategoryPanel(folderModel, mailList, cardPanel, cardLayout);

        cardPanel.add(infoPanel, "infoPanel");
        cardPanel.add(createDetailPanel(cardLayout, cardPanel), "detailPanel");

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, categoryPanel, cardPanel);
        splitPane.setDividerLocation(300);
        return splitPane;
    }
}