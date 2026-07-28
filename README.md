
# Face Landmarker - App Android Teste

Modelo: face_landmarker.task
URL: https://storage.googleapis.com/mediapipe-models/face_landmarker/face_landmarker/float16/1/face_landmarker.task

PASSOS:
1. Abra no Android Studio
2. Crie a pasta app/src/main/assets/ 
3. Baixe o modelo e coloque como face_landmarker.task dentro de assets
   Ou use wget: 
   wget -O app/src/main/assets/face_landmarker.task https://storage.googleapis.com/mediapipe-models/face_landmarker/face_landmarker/float16/1/face_landmarker.task

4. Sync Gradle e Run no celular.

OBS: Para converter YUV perfeito, se der tela preta, substitua YuvToRgbConverter.kt pelo oficial do Google:
https://raw.githubusercontent.com/googlesamples/mlkit/master/android/vision-quickstart/app/src/main/java/com/google/mlkit/vision/demo/YuvToRgbConverter.kt

Licença Apache 2.0 - tudo gratuito e on-device.
