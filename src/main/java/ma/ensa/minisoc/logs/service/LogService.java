package ma.ensa.minisoc.logs.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ma.ensa.minisoc.logs.Repository.LogRepository;
import ma.ensa.minisoc.logs.model.LogEntity;

import java.util.List;

@Service
public class LogService {

    @Autowired
    private LogRepository logRepository;

    public void saveLogs(List<LogEntity> logs) {
        logRepository.saveAll(logs);
    }

    public List<LogEntity> getAllLogs(){
        return logRepository.findAll();
    }
    
    public void save(LogEntity log) {
        logRepository.save(log);
    }

    public Long totalLogs()
    {
        return logRepository.count();
    }
}
