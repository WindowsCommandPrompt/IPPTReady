package sg.np.edu.mad.ipptready.ExerciseTogether;

public interface QRCodeFoundListener {
    // Variables for constructor of QRCodeImageAnalyzer
    void onQRCodeFound(String qrCode);
    void qrCodeNotFound();
}
