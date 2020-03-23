# orbslam

## Monocular

循环读取每一帧图片，调用GrabImageMonocular

判断系统是否已经初始化，构造Frame

mpIniORBextractor  mpORBextractorLeft  区别：特征点个数

如果尚未初始化，调用MonocularInitialization

如果已经初始化了，则track



## Frame