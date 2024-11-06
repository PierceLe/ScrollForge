package org.scrollSystem.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.scrollSystem.exception.ValidationException;
import org.scrollSystem.models.FileStorage;
import org.scrollSystem.models.Setting;
import org.scrollSystem.models.User;
import org.scrollSystem.repository.FileStorageRepository;
import org.scrollSystem.repository.SettingRepo;
import org.scrollSystem.repository.UserRepository;
import org.scrollSystem.request.FileRequest;
import org.scrollSystem.request.Timer;
import org.scrollSystem.response.FileResponse;
import org.scrollSystem.response.UserResponse;
import org.scrollSystem.utility.LogMessageFormatter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Getter
public class ScrollService {
    private final FileStorageRepository fileRepository;
    private final S3Service s3Service;
    private final UserService userService;
    private final UserRepository userRepository;
    private final SettingRepo settingRepo;

    private static final Logger actionLogger = LoggerFactory.getLogger("com.example.specific");

    @Value("${logging.file.specific}")
    private String logFilePath;

    @Transactional
    public FileResponse uploadFile(MultipartFile file, String title) throws IOException {
        Optional<FileStorage> optionalFileStorage = fileRepository.getFileStorageByTitle(title);

        if (optionalFileStorage.isPresent()) {
            throw new ValidationException("File title is exists");
        }

        String filePath = s3Service.uploadFile(file);

        User owner = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer timer = settingRepo.findAll().get(0).getTimer();
        FileStorage fileStorage = FileStorage.builder()
                .title(title)
                .filePath(filePath)
                .fileSize(file.getSize())
                .fileType(file.getContentType())
                .owner(owner)
                .timer(timer)
                .build();
        fileRepository.save(fileStorage);

        owner.setUploadNumber(owner.getUploadNumber() + 1);
        String message = " uploaded file titled: " + title;
        logAction(owner.getUsername(), message);

        userRepository.save(owner);

        FileResponse response = makeFileResponse(fileStorage);

        UserResponse ownerResponse = userService.getUserResponse(owner);
        response.setOwner(ownerResponse);

        return response;
    }

    private FileResponse makeFileResponse(FileStorage fileStorage) {
        return FileResponse.builder()
                .fileId(fileStorage.getFileId())
                .title(fileStorage.getTitle())
                .filePath(fileStorage.getFilePath())
                .fileSize(fileStorage.getFileSize())
                .fileType(fileStorage.getFileType())
                .uploadDate(fileStorage.getUploadDate())
                .downloadAmount(fileStorage.getDownloadAmount())
                .timer(fileStorage.getTimer())
                .build();
    }

    private void logAction(String username, String message) throws IOException {
        // Ensure the log file directory exists
        Path logFilePathDir = Paths.get(logFilePath).getParent();
        if (logFilePathDir != null && Files.notExists(logFilePathDir)) {
            Files.createDirectories(logFilePathDir);
        }

        // Create a logger for the specific action and set the file handler
        java.util.logging.Logger fileLogger = java.util.logging.Logger.getLogger("ScrollLogger");
        FileHandler fileHandler = new FileHandler(logFilePath, true); // append mode = true
        fileHandler.setFormatter(new LogMessageFormatter()); // Use the custom formatter

        // Attach the file handler to the logger
        fileLogger.addHandler(fileHandler);
        fileLogger.setLevel(Level.INFO);
        fileLogger.setUseParentHandlers(false); // Prevent it from logging to the console

        // Format the log message with title and date
        String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String logEntry = String.format("{\"title\":\"%s uploaded file titled: %s\", \"date\":\"%s\"}", username, message, currentDateTime);

        fileLogger.info(logEntry);

        fileHandler.close();
    }

    public List<Map<String, Object>> getLogs() throws IOException {
        List<Map<String, Object>> logEntries = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");  // Define the expected date format

        try (BufferedReader reader = new BufferedReader(new FileReader(logFilePath))) {
            String line;
            ObjectMapper mapper = new ObjectMapper();

            while ((line = reader.readLine()) != null) {
                try {
                    // Parse each line
                    Map<String, Object> logEntry = mapper.readValue(line, new TypeReference<Map<String, Object>>() {});

                    // Parse the date string and format it
                    String dateString = (String) logEntry.get("date");
                    LocalDateTime parsedDate = LocalDateTime.parse(dateString, formatter);

                    // Replace the date string with the parsed LocalDateTime object
                    logEntry.put("date", parsedDate);

                    logEntries.add(logEntry);
                } catch (DateTimeParseException e) {
                    System.err.println("Invalid date format in log entry: " + line);
                } catch (JsonProcessingException e) {
                    System.err.println("Failed to parse log entry: " + line);
                }
            }
        } catch (IOException e) {
            throw new IOException("Failed to read log file.", e);
        }

        // Sort by date in descending order (most recent first)
        logEntries.sort((a, b) -> {
            LocalDateTime dateA = (LocalDateTime) a.get("date");
            LocalDateTime dateB = (LocalDateTime) b.get("date");
            return dateB.compareTo(dateA);
        });

        return logEntries.stream().limit(50).collect(Collectors.toList());
    }


    @Transactional
    public Integer deleteFile(Integer id) throws IOException {
        Optional<FileStorage> optionalFileStorage = fileRepository.getFileStorageByFileId(id);

        if (optionalFileStorage.isEmpty()) {
            throw new ValidationException("File id is not exists");
        }
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer user_id = user.getId();

        FileStorage fileStorage = optionalFileStorage.get();

        if (!fileStorage.getOwner().getId().equals(user_id) && user.getRole().equals("ROLE_USER")) {
            throw new ValidationException("You are not the owner of the file so you can not delete this scroll");
        }

        s3Service.deleteFile(fileStorage.getTitle());
        fileRepository.delete(fileStorage);

        String message =  " deleted file titled: " + fileStorage.getTitle();
        logAction(user.getUsername(), message);

        return id;
    }


    public List<FileResponse> getScrolls() {
        List<FileResponse> response = new ArrayList<>();

        List<FileStorage> scrollsList = fileRepository.findAll();
        for (FileStorage fileStorage: scrollsList) {
            FileResponse scrollResponse = FileResponse.builder()
                    .fileId(fileStorage.getFileId())
                    .title(fileStorage.getTitle())
                    .filePath(fileStorage.getFilePath())
                    .fileSize(fileStorage.getFileSize())
                    .fileType(fileStorage.getFileType())
                    .uploadDate(fileStorage.getUploadDate())
                    .downloadAmount(fileStorage.getDownloadAmount())
                    .build();
            User owner = fileStorage.getOwner();
            UserResponse userResponse = UserResponse.builder()
                    .id(owner.getId())
                    .firstName(owner.getFirstName())
                    .lastName(owner.getLastName())
                    .email(owner.getEmail())
                    .username(owner.getUsername())
                    .phone(owner.getPhone())
                    .role(owner.getRole())
                    .build();

            scrollResponse.setOwner(userResponse);
            response.add(scrollResponse);
        }
        return response;
    }

    public List<FileResponse> getSearchFilter(String title, String fileType, String ownerUsername, Integer fileId,
                                           Timestamp fromDate, Timestamp toDate) {
        List<FileResponse> response = new ArrayList<>();
        Optional<User> user = userRepository.findByUsername(ownerUsername);
        if (user.isEmpty() && ownerUsername != null) {
            return response;
        }
        Integer user_id = null;
        if (ownerUsername != null){
            user_id = user.get().getId();
        }
        List<FileStorage> scrollsList = fileRepository.filterbyField(title, fileType, user_id, fileId, fromDate, toDate);
        System.out.println(scrollsList.size() + " "+ title);
        for (FileStorage fileStorage: scrollsList) {
            FileResponse scrollResponse = makeFileResponse(fileStorage);

            User owner = fileStorage.getOwner();
            UserResponse userResponse = UserResponse.builder()
                    .id(owner.getId())
                    .firstName(owner.getFirstName())
                    .lastName(owner.getLastName())
                    .email(owner.getEmail())
                    .username(owner.getUsername())
                    .phone(owner.getPhone())
                    .role(owner.getRole())
                    .build();

            scrollResponse.setOwner(userResponse);
            response.add(scrollResponse);
        }
        return response;
    }


    public String download(Integer id) {
        Optional<FileStorage> file = fileRepository.getFileStorageByFileId(id);

        if (file.isEmpty()) {
            throw new ValidationException("File id " + id + " is not exist");
        }

        FileStorage fileStorage = file.get();
        fileStorage.increaseDownload();
        fileRepository.save(fileStorage);

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user.setDownloadNumber(user.getDownloadNumber() + 1);
        userRepository.save(user);

        return fileStorage.getFilePath();
    }

    @Transactional
    public FileResponse update(FileRequest request) throws IOException {
        FileStorage fileStorage = fileRepository.getFileStorageByFileId(request.getId())
                .orElseThrow(() -> new ValidationException("File id " + request.getId() + " is not exist"));

        Optional<FileStorage> checkTitle = fileRepository.getFileStorageByTitle(request.getTitle());

        if (checkTitle.isPresent() && !Objects.equals(checkTitle.get(), fileStorage)) {
            throw new ValidationException("File title is exists");
        }

        fileStorage.setTitle(request.getTitle());
        fileStorage.setTimer(request.getTimer());

        String filePath = s3Service.uploadFile(request.getFile());
        fileStorage.setFilePath(filePath);
//        fileStorage.increaseUpload();

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String message = " updated file titled: " + request.getTitle();
        logAction(user.getUsername(), message);

        fileRepository.save(fileStorage);

        return makeFileResponse(fileStorage);
    }

    @Transactional
    public String updateTime(Timer request) {
        Setting setting = new Setting();
        setting.setTimer(request.getTimer());
        settingRepo.truncateSettingTable();
        settingRepo.save(setting);
        updateTimeForAllScrolls(request.getTimer());
        return "SUCESS";
    }

    @Transactional
    public void updateTimeForAllScrolls(Integer time) {
        fileRepository.updateScrollTimerForAll(time);
    }

}
