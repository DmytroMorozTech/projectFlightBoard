package app.service.fileSystemService;

import java.io.*;

public class FileSystemService {
    public Object getDataFromFile(String nameOfFile) throws IOException, ClassNotFoundException {
        File file = new File(nameOfFile);
        boolean fileIsFound = file.isFile();
        if (!fileIsFound) {
            System.out.println("Не удалось считать данные из файла " + nameOfFile);
            throw new IOException();
        }

        FileInputStream fis = new FileInputStream(file);
        ObjectInputStream ois = new ObjectInputStream(fis);
        Object receivedData = ois.readObject();
        ois.close();
        fis.close();

        return receivedData;
        // возвращаем объект, который программа считала из файла
    }

    public void saveDataToFile(String nameOfFile, Object data) throws IOException {
        File file = new File(nameOfFile);
        boolean fileIsFound = file.isFile();
        if (!fileIsFound) {
            throw new IOException("Возникла ошибка при сохранении файла" + nameOfFile);
        }

        FileOutputStream fos = new FileOutputStream(file);
        ObjectOutputStream oos = new ObjectOutputStream(fos);

        oos.writeObject(data);

        oos.close();
        fos.close();
    }

}
