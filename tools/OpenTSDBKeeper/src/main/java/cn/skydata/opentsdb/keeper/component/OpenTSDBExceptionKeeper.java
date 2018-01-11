package cn.skydata.opentsdb.keeper.component;

import cn.skydata.opentsdb.keeper.exception.NonAliveOpenTSDBException;
import cn.skydata.opentsdb.keeper.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OpenTSDBExceptionKeeper {

    @Autowired
    private MailService mailService;

    public NonAliveOpenTSDBException nonAliveOpenTSDBException() {
        String content = "All your OpenTSDB Client dead. OpenTSDB Keeper System now shut down";
        mailService.sendMail("OpenTSDB Warning", content);
        log.error(content);
        return new NonAliveOpenTSDBException();
    }
}
