package service;

import dao.FileDao;


public class FileService {

    private static FileService service = new FileService();
    private static FileDao dao = FileDao.getFileDao();
    public static FileService getFileService() {
        return service;
    }

}
