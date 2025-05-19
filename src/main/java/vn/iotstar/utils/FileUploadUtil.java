package vn.iotstar.utils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
public class FileUploadUtil
{
	 	private static final String UPLOAD_DIR = "uploads/";
	    public static String saveFile(MultipartFile file) throws IOException {
	    if (file.isEmpty()) 
	    {
	        throw new IOException("Không có file được chọn để tải lên.");
	    }
	        
	    Path uploadPath = Paths.get(UPLOAD_DIR);
	        if (!Files.exists(uploadPath)) {
	            Files.createDirectories(uploadPath);
	        }

	        // Tạo tên file duy nhất để tránh trùng
	        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
	        Path filePath = uploadPath.resolve(fileName);
	        file.transferTo(filePath.toFile());
	        return UPLOAD_DIR + fileName;
	    }
}
