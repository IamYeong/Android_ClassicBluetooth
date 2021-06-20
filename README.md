## ClassicBluetoothApp
Bluetooth v 4.0 미만에서 지원하는 Classic Bluetooth 데이터 송/수신 앱입니다.

#### 구현 순서     

1. 권한확인    
- Intent(Context, BluetoothAdapter.bluetooth상수); 해당코드로 Intent를 생성한다.    
- startActivityForResult(Intent, 지정 상수);    
해당코드를 onStart() 즈음 실행시켜서 내가 요청한 상수로 코드가 넘어왔는지 확인 후,     
사용자가 Bluetooth 권한요청을 한 Intent에 대해서 동의했는지 안 했는지, Activity 에 정의된 RESULT_OK 상수를 활용하여 확인한다.    

2. BluetoothAdapter 얻기    
- 여기는 쉽다. getDefaultAdapter() 로 얻어오면 된다.     

3. Scan하고 BroadcastReceiver 로 확인하기    
- 우선 IntentFilter() 를 BluetoothDevice 안에 있는 DEVICE_FOUND 액션으로 되어있는 상수와 함께 생성한다.    
- IntentFilter가 생겼으니 결과를 받고자 하는 Activity에서 미리 동작을 설정한 Receiver 변수를 넣어서 등록한다.    
(registerReceiver(IntentFilter, BroadcastReceiver))    

4. BluetoothDevice 정보 확인하기    
- Scan 은 Device가 근처에 있는지 확인하고, 있다면 정보를 얻어오기 위한 작업일 뿐이다.   
- 미리 페어링돼있는 기기 먼저 확인 후 주변기기를 스캔하고, 어디에든 찾는 기기가 있었다면     
deviceName() 과 deviceAddress() 를 가져온다.     

5. 연결시도하기    
- getRemoteDevice(Mac address) 를 통해서 주변에 있는 걸 확인한 디바이스의 객체를 가져온다.    
- 가져왔다면 device.connectComm ~ 메서드를 통해 BluetoothSocket 객체를 가져온다.     
- Socket을 성공적으로 가져왔다면 connect() 메서드를 호출하여 실제로 연결한다.    
- 성공적으로 연결됐다면 getInputStream() 과 getOutputStream() 을 사용하여 Stream 정보를 가져온다.     

6. 데이터 읽기    
- InputStream 의 available() 을 통해 Stream에 읽을 byte가 있는지 확인하고,    
read() 메서드를 통해 byte[] 배열에 byte 데이터들을 할당해준다.    
반복적으로 받아야한다면 while() 과 같은 반복문을 확인하여 동작을 반복하고, 특정 행동을 취하면 된다.    

7. 데이터 쓰기    
- OutputStream write() 를 사용하여 byte 를 써주면 된다.    

8. 주의사항    
- Stream의 경우에는 꼭 스레드를 따로따로 설정해주어야 한다.    
- connect() 동작들도 모두 별개의 스레드를 사용해야 한다.    
