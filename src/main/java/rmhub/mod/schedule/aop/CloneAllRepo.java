package rmhub.mod.schedule.aop;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class CloneAllRepo {
    public static void main(String[] args) {
        File file = new File("lsrepo.txt");
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(file);
            JsonNode repositories = rootNode.path("repositories");
            // Đường dẫn thư mục gốc để clone repository về
            String destinationFolderBase = "C:\\Users\\CuongNV37\\Cuongnv99\\code_MSB";
            for (JsonNode repo : repositories) {
                String repoName = repo.path("repositoryName").asText();
                // Tạo thư mục đích dựa trên tên repository
                String destinationFolder = destinationFolderBase + File.separator + repoName;

                // Kiểm tra xem thư mục đích đã tồn tại chưa
                File destinationDir = new File(destinationFolder);
                if (destinationDir.exists()) {
                    System.out.println("Thư mục " + repoName + " đã tồn tại. Bỏ qua repository này.");
                    continue;  // Bỏ qua repository nếu thư mục đã tồn tại
                } else {
                    // Tạo thư mục mới cho repository nếu thư mục chưa tồn tại
                    destinationDir.mkdir();
                    // Đường dẫn repo
                    String repoUrl = String.format("https://git-codecommit.ap-southeast-1.amazonaws.com/v1/repos/%s", repoName);

                    // Sử dụng ProcessBuilder để chạy lệnh git clone
                    ProcessBuilder processBuilder = new ProcessBuilder("git", "clone", repoUrl, destinationFolder);
                    processBuilder.inheritIO();
                    Process process = processBuilder.start();
                    process.waitFor();
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}