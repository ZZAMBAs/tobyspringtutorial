package com.example.tobyspringtutorial.modules.service;

import com.example.tobyspringtutorial.modules.objects.Level;
import com.example.tobyspringtutorial.modules.objects.User;
import com.example.tobyspringtutorial.modules.repository.UserDao;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import static com.example.tobyspringtutorial.modules.service.UserService.MIN_LOGCOUNT_FOR_SILVER;
import static com.example.tobyspringtutorial.modules.service.UserService.MIN_RECCOUNT_FOR_GOLD;

public class UserServicePolicyDefault implements UserServicePolicy{ // 평소 업그레이드 정책.
    private UserDao userDao;
    protected MailSender mailSender;

    public void setUserDao(UserDao userDao){
        this.userDao = userDao;
    }

    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    public boolean canUpgradeLevel(User user){
        Level userLevel = user.getLevel();
        switch (userLevel){
            case BASIC: return (user.getLogin() >= MIN_LOGCOUNT_FOR_SILVER);
            case SILVER: return (user.getRecommend() >= MIN_RECCOUNT_FOR_GOLD);
            case GOLD: return false;
            default: throw new IllegalArgumentException("Unknown Level: " + userLevel); // 언체크 예외
                // 이 예외는 새 레벨이 추가 되었을 때, 개발자가 이 코드를 수정하지 않았을 경우 발생하여 코드를 수정하도록 돕는다.
        }
    }

    public void upgradeLevel(User user){
        user.upgradeLevel();
        userDao.update(user); // DB에도 적용하는 것을 잊지 말자.
        sendUpgradeEmail(user); // SRP에 의해 이곳에 메일 보내는 코드를 직접 추가하지 않도록 한다.
    }

    private void sendUpgradeEmail(User user) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setFrom("useradmin@ksug.org");
        mailMessage.setSubject("Upgrade 안내");
        mailMessage.setText("사용자님의 등급이 " + user.getLevel().name());

        this.mailSender.send(mailMessage);
    }

}
