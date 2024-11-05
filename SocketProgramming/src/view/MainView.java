package view;


import view.AccConnectView;
import controller.GmailConnector;
import controller.NaverConnector;
import model.EmailDataRepository;
import model.GoogleUserInfoDTO;
import model.NaverUserInfoDTO;
import view.ContentMailPanel;
import view.SenderFrame;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.List;

public class MainView {
    private JFrame mainFrame;
    private String nickname;
    private JPanel detailPanel;
    private JLabel senderLabel;
    private JLabel subjectLabel;
    private JPanel infoPanel;
    private JLabel timeLabel;
    private JTextArea messageContent;
    private NaverUserInfoDTO naverUserInfoDTO;
    private GoogleUserInfoDTO googleUserInfoDTO;
    private ContentMailPanel contentMailPanel;
    private int NAVER = 1;
    private int GOOGLE = 2;
    private int BOTH = 3;

    public MainView(String userId) {
        this.nickname = userId;
        this.naverUserInfoDTO = new NaverUserInfoDTO();
        this.googleUserInfoDTO = new GoogleUserInfoDTO();
        googleUserInfoDTO.setUsername("tkdgur9799@gmail.com");
        googleUserInfoDTO.setPassword("nolb vtfr mqls hnjj");
    }

    public void createMainFrame() {
        mainFrame = new JFrame("환영합니다 " + nickname + "님!!");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mainFrame.setSize(1200, 700);
        mainFrame.setLayout(new BorderLayout());

        JPanel topPanel = createTopPanel();
        mainFrame.add(topPanel, BorderLayout.NORTH);

        JSplitPane splitPane = createSplitPane();
        mainFrame.add(splitPane, BorderLayout.CENTER);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());

        JButton sendMailButton = new JButton("메일 보내기");
        JButton verifyEmailButton = new JButton("이메일 인증하기");
        JButton refreshButton = new JButton("새로 고침");

        topPanel.add(sendMailButton, BorderLayout.WEST);
        topPanel.add(verifyEmailButton, BorderLayout.EAST);
        topPanel.add(refreshButton, BorderLayout.CENTER);

        createSendMailButtonEvent(sendMailButton);
        createVerifyEmailButtonEvent(verifyEmailButton);
        createRefreshButtonEvent(refreshButton);

        return topPanel;
    }

    private void createRefreshButtonEvent(JButton button) {
        button.addActionListener(e -> {
            if ((naverUserInfoDTO.getUsername() != null && naverUserInfoDTO.getPassword() != null) && (googleUserInfoDTO.getUsername() == null && googleUserInfoDTO.getPassword() == null)) {
                loadEmailsInBackground(NAVER);
            } else if ((naverUserInfoDTO.getUsername() == null && naverUserInfoDTO.getPassword() == null) && (googleUserInfoDTO.getUsername() != null && googleUserInfoDTO.getPassword() != null)) {
                loadEmailsInBackground(GOOGLE);
            } else if ((naverUserInfoDTO.getUsername() != null && naverUserInfoDTO.getPassword() != null) && (googleUserInfoDTO.getUsername() != null && googleUserInfoDTO.getPassword() != null)) {
                loadEmailsInBackground(NAVER);
                loadEmailsInBackground(GOOGLE);
            } else {
                JOptionPane.showMessageDialog(null, "메일 계정을 1개 이상 연동해주세요.");
            }
        });
    }

    public void loadEmailsInBackground(int browser) {
        final JDialog loadingDialog = new JDialog(mainFrame, "로딩 중...", false);
        loadingDialog.setSize(200, 100);
        loadingDialog.setLocationRelativeTo(mainFrame);
        loadingDialog.add(new JLabel("메일을 불러오는 중입니다...", SwingConstants.CENTER));

        // SwingWorker 실행 전 로딩 대화창을 먼저 표시
        loadingDialog.setVisible(true);

        // 백그라운드에서 메일을 로드하는 작업 실행
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                if (browser == NAVER) {
                    getNaverEmails();
                } else if (browser == GOOGLE) {
                    getGoogleEmails();
                } else if (browser == BOTH) {
                    getNaverEmails();
                    getGoogleEmails();
                }
                return null;
            }

            @Override
            protected void done() {
                // 로딩 패널 제거 및 메일 목록 UI 표시
                loadingDialog.dispose();
            }
        };
        worker.execute();
    }

    private void getNaverEmails() {
        try {
            String username = naverUserInfoDTO.getUsername();
            String password = naverUserInfoDTO.getPassword();

            NaverConnector naverConnector = new NaverConnector(username, password);

            naverConnector.fetchAllMailFolders();
            naverConnector.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getGoogleEmails() {
        try {
            String username = googleUserInfoDTO.getUsername();
            String password = googleUserInfoDTO.getPassword();

            GmailConnector gmailConnector = new GmailConnector(username, password);
            gmailConnector.fetchAllMailFolders();
            gmailConnector.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showMessageInCardPanel(JPanel cardPanel, CardLayout cardLayout, String message) {
        JPanel messagePanel = new JPanel(new BorderLayout());
        JLabel messageLabel = new JLabel(message, SwingConstants.CENTER);
        messagePanel.add(messageLabel, BorderLayout.CENTER);

        cardPanel.add(messagePanel, "messagePanel");
        cardLayout.show(cardPanel, "messagePanel");
    }

    //메일 보내기 버튼 이벤트 생성 함수
    private void createSendMailButtonEvent(JButton sendMailButton) {
        sendMailButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //메일 보내기 프레임 생성 함수
                showSendMailPopup();
            }
        });
    }

    //여기에 메일 보내기 FRAME 집어 넣으면 됨(상혁 파트)
    private void showSendMailPopup() {
        String platform = (String) JOptionPane.showInputDialog(null, "메일을 보낼 플랫폼을 선택하세요.", "메일 보내기", JOptionPane.QUESTION_MESSAGE, null, new String[]{"네이버", "구글"}, "네이버");
        String username = null, password = null;

        if (platform == null || platform.isEmpty()) {
            return;
        }

        switch (platform) {
            case "네이버":
                username = this.naverUserInfoDTO.getUsername();
                password = this.naverUserInfoDTO.getPassword();
                break;
            case "구글":
                username = this.googleUserInfoDTO.getUsername();
                password = this.googleUserInfoDTO.getPassword();
                break;
            default:
                break;
        }

        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(null, platform + " 계정을 연동해주세요.");
            return;
        }

        new SenderFrame(username, password);
    }

    //이메일 인증 버튼 이벤트 함수
    private void createVerifyEmailButtonEvent(JButton verifyEmailButton) {
        verifyEmailButton.addActionListener(e -> {
            //이메일 인증 프레임 생성 함수
            new AccConnectView(nickname, naverUserInfoDTO, googleUserInfoDTO).createAccConnectView();
        });
    }

    private JPanel createCategoryPanel(DefaultListModel<String> naverFolderModel, DefaultListModel<String> googleFolderModel, JList<String[]> naverMailList, JList<String[]> googleMailList, JPanel cardPanel, CardLayout cardLayout) {
        // 네이버 메일 폴더 목록 생성
        JList<String> naverFolderList = new JList<>(naverFolderModel);
        JList<String> googleFolderList = new JList<>(googleFolderModel);
        naverFolderList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        naverFolderList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedFolder = naverFolderList.getSelectedValue();
                if (naverUserInfoDTO.getUsername() == null || naverUserInfoDTO.getPassword() == null) {
                    showMessageInCardPanel(cardPanel, cardLayout, "네이버 계정을 연동해주세요.");
                } else {
                    updateMailList(naverMailList, googleMailList, selectedFolder);
                    cardLayout.show(cardPanel, "infoPanel");

                    if ("받은메일함".equals(selectedFolder)) {
                        contentMailPanel.updateIndex(0);
                        updateMailList(naverMailList, googleMailList, selectedFolder);
                        cardLayout.show(cardPanel, "infoPanel");
                    } else if ("보낸메일함".equals(selectedFolder)) {
                        contentMailPanel.updateIndex(1);
                        updateMailList(naverMailList, googleMailList, selectedFolder);
                        cardLayout.show(cardPanel, "infoPanel");
                    }
                }

            }
        });

        // 구글 메일 폴더 목록 생성

        googleFolderList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        googleFolderList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedFolder = googleFolderList.getSelectedValue();
                if (googleUserInfoDTO.getUsername() == null || googleUserInfoDTO.getPassword() == null) {
                    showMessageInCardPanel(cardPanel, cardLayout, "구글 계정을 연동해주세요.");
                } else {
                    updateMailList(naverMailList, googleMailList, selectedFolder);
                    cardLayout.show(cardPanel, "infoPanel");
                }
            }
        });

        // 네이버 폴더와 구글 폴더 각각에 스크롤 패널 추가
        JScrollPane naverScrollPane = new JScrollPane(naverFolderList);
        JScrollPane googleScrollPane = new JScrollPane(googleFolderList);

        // 네이버와 구글 폴더를 포함하는 패널 생성
        JPanel naverPanel = new JPanel(new BorderLayout());
        naverPanel.add(new JLabel("네이버 메일 폴더", JLabel.LEFT), BorderLayout.NORTH);
        naverPanel.add(naverScrollPane, BorderLayout.CENTER);

        JPanel googlePanel = new JPanel(new BorderLayout());
        googlePanel.add(new JLabel("구글 메일 폴더", JLabel.LEFT), BorderLayout.NORTH);
        googlePanel.add(googleScrollPane, BorderLayout.CENTER);

        // 네이버와 구글 패널을 JSplitPane으로 나누어 상단과 하단으로 배치
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, naverPanel, googlePanel);
        splitPane.setResizeWeight(0.5); // 상하 패널 크기 비율 설정
        splitPane.setDividerSize(5);    // 구분선 크기 설정

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(splitPane, BorderLayout.CENTER);
        return panel;
    }

    private void updateInBoxNaver(List<String[]> mails) {
        for (String[] mail : EmailDataRepository.getInstance().getNaverInBoxMailData()) {
            mails.add(new String[]{mail[0], mail[1], mail[2], mail[3], mail[4]});
        }
    }

    private void updateInBoxGoogle(List<String[]> mails) {
        for (String[] mail : EmailDataRepository.getInstance().getGoogleInBoxMailData()) {
            mails.add(new String[]{mail[0], mail[1], mail[2], mail[3], mail[4]});
        }
    }

    private void updateSentBoxNaver(List<String[]> mails) {
        for (String[] mail : EmailDataRepository.getInstance().getNaverSentMailData()) {
            mails.add(new String[]{mail[0], mail[1], mail[2], mail[3], mail[4]});
        }
    }

    private void updateSentBoxGoogle(List<String[]> mails) {
        for (String[] mail : EmailDataRepository.getInstance().getGoogleSentMailData()) {
            mails.add(new String[]{mail[0], mail[1], mail[2], mail[3], mail[4]});
        }
    }

    private void updateDraftsBoxNaver(List<String[]> mails) {
        for (String[] mail : EmailDataRepository.getInstance().getNaverDraftMailData()) {
            mails.add(new String[]{mail[0], mail[1], mail[2], mail[3], mail[4]});
        }
    }

    private void updateDraftBoxGoogle(List<String[]> mails) {
        for (String[] mail : EmailDataRepository.getInstance().getGoogleDraftMailData()) {
            mails.add(new String[]{mail[0], mail[1], mail[2], mail[3], mail[4]});
        }
    }

    private void updateTrashBoxNaver(List<String[]> mails) {
        for (String[] mail : EmailDataRepository.getInstance().getNaverTrashMailData()) {
            mails.add(new String[]{mail[0], mail[1], mail[2], mail[3], mail[4]});
        }
    }

    private void updateTrashBoxGoogle(List<String[]> mails) {
        for (String[] mail : EmailDataRepository.getInstance().getGoogleTrashMailData()) {
            mails.add(new String[]{mail[0], mail[1], mail[2], mail[3], mail[4]});
        }
    }

    private void updateMailList(JList<String[]> naverMailList, JList<String[]> googleMailList, String folderName) {
        DefaultListModel<String[]> naverListModel = new DefaultListModel<>();
        DefaultListModel<String[]> googleListModel = new DefaultListModel<>();

        List<String[]> naverMails = new ArrayList<>();
        List<String[]> googleMails = new ArrayList<>();

        JScrollPane activeScrollPane = null; // 현재 표시할 스크롤 패널

        switch (folderName) {
            case "받은메일함":
                updateInBoxNaver(naverMails);
                activeScrollPane = new JScrollPane(naverMailList);
                break;
            case "보낸메일함":
                updateSentBoxNaver(naverMails);
                activeScrollPane = new JScrollPane(naverMailList);
                break;
            case "임시보관함":
                updateDraftsBoxNaver(naverMails);
                activeScrollPane = new JScrollPane(naverMailList);
                break;
            case "휴지통":
                updateTrashBoxNaver(naverMails);
                activeScrollPane = new JScrollPane(naverMailList);
                break;
            case "g받은메일함":
                updateInBoxGoogle(googleMails);
                System.out.println(EmailDataRepository.getInstance().getGoogleInBoxMailData());
                activeScrollPane = new JScrollPane(googleMailList);
                break;
            case "g보낸메일함":
                updateSentBoxGoogle(googleMails);
                System.out.println(EmailDataRepository.getInstance().getGoogleSentMailData());
                activeScrollPane = new JScrollPane(googleMailList);
                break;
            case "g임시보관함":
                updateDraftBoxGoogle(googleMails);
                activeScrollPane = new JScrollPane(googleMailList);
                break;
            case "g휴지통":
                updateTrashBoxGoogle(googleMails);
                activeScrollPane = new JScrollPane(googleMailList);
                break;
        }

        // Populate models
        for (int i = naverMails.size() - 1; i >= 0; i--) {
            naverListModel.addElement(naverMails.get(i));
        }

        for (int i = googleMails.size() - 1; i >= 0; i--) {
            googleListModel.addElement(googleMails.get(i));
        }

        naverMailList.setModel(naverListModel);
        googleMailList.setModel(googleListModel);

        // `infoPanel`의 내용을 동적으로 변경
        if (activeScrollPane != null) {
            infoPanel.removeAll(); // 이전 내용 제거
            infoPanel.add(activeScrollPane, BorderLayout.CENTER); // 새로운 메일 리스트 스크롤 패널 추가
            infoPanel.revalidate();
            infoPanel.repaint();
        }
    }


    private JList<String[]> createMailList(JPanel cardPanel, CardLayout cardLayout, int browser) {
        DefaultListModel<String[]> listModel = new DefaultListModel<>();
        JList<String[]> mailList = new JList<>(listModel);

        // 커스텀 렌더러 설정
        mailList.setCellRenderer(new ListCellRenderer<String[]>() {
            @Override
            public Component getListCellRendererComponent(JList<? extends String[]> list, String[] value, int index, boolean isSelected, boolean cellHasFocus) {
                JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
                panel.setPreferredSize(new Dimension(500, 60));

                JLabel senderLabel = new JLabel("보낸 사람: " + value[0]);
                JLabel subjectLabel = new JLabel("제목: " + value[2]);
                JLabel timeLabel = new JLabel("시간: " + value[3]);

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

                        if (browser == NAVER) {
                            contentMailPanel.updateValue(value, naverUserInfoDTO.getUsername(), naverUserInfoDTO.getPassword());
                        } else if (browser == GOOGLE) {
                            contentMailPanel.updateValue(value,googleUserInfoDTO.getUsername(), googleUserInfoDTO.getPassword());

                        }
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
        messageContent = new JTextArea("");
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

    private void updateDetailPanel(String sender, String receiver, String subject, String time, String text) {
        senderLabel.setText("보낸 사람: " + sender);
        subjectLabel.setText("제목: " + subject);
        timeLabel.setText("시간: " + time);
        messageContent.setText(text);  // 실제 메일 내용을 여기에 표시할 수 있습니다.
    }

    private JSplitPane createSplitPane() {
        DefaultListModel<String> naverFolderModel = new DefaultListModel<>();
        naverFolderModel.addElement("받은메일함");
        naverFolderModel.addElement("보낸메일함");
        naverFolderModel.addElement("임시보관함");
        naverFolderModel.addElement("휴지통");

        DefaultListModel<String> googleFolderModel = new DefaultListModel<>();
        googleFolderModel.addElement("g받은메일함");
        googleFolderModel.addElement("g보낸메일함");
        googleFolderModel.addElement("g임시보관함");
        googleFolderModel.addElement("g휴지통");

        CardLayout cardLayout = new CardLayout();
        JPanel cardPanel = new JPanel(cardLayout);
        JList<String[]> naverMailList = createMailList(cardPanel, cardLayout, NAVER);
        JList<String[]> googleMailList = createMailList(cardPanel, cardLayout, GOOGLE);

        infoPanel = new JPanel(new BorderLayout());
        infoPanel.add(new JScrollPane(naverMailList), BorderLayout.CENTER);

        JPanel categoryPanel = createCategoryPanel(naverFolderModel, googleFolderModel, naverMailList, googleMailList, cardPanel, cardLayout);

        contentMailPanel = new ContentMailPanel(cardLayout, cardPanel);

        cardPanel.add(infoPanel, "infoPanel");
        cardPanel.add(contentMailPanel, "detailPanel");

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, categoryPanel, cardPanel);
        splitPane.setDividerLocation(300);
        return splitPane;
    }

}