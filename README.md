##### Multithread Documentation

This is branch is a multithreaded variant of the single core version of the version on the master branch

 Possible Commandline Arguments:
 
    ` -threads`     number of Threads the program should create to render the image
    ` -sampleRate`  the number of Rays that are being shot per pixel
    `-bounces`     the number of bounces per single ray
     
All Arguments are optional. If left blank these Standard Values are taken:

    threads = 1
    sampleRate = 32 
    bounces = 5

Sample Arguments

`java -jar CornellBox.jar -threads='8' -sampleRate='128'-bounces='5'`