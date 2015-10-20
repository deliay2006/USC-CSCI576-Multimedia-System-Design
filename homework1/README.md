# USC-CSCI576-Multimedia-System-Design-Homework1
University of Southern California CSCI-576 Multimedia System Design Homework1

1- In Mac Operation System
2- Need install JDK.
3- Open “Terminal” command line tool.
4- Go to the folder “Programming Part”
5- Compile the Java file first: $ javac SubSampleQuan.java
6- Run result test like:$ java SubSampleQuan image2.rgb 1 1 1 8


CS 576 – Assignment 1 Instructor: Parag Havaldar
Q.1 Suppose a camera has 450 lines per frame, 520 pixels per line, and 25 Hz frame rate. The color sub sampling scheme is 4:2:0, and the pixel aspect ratio is 16:9. The camera uses interlaced scanning, and each sample of Y, Cr, Cb is quantized with 8 bits
 What is the bit-rate produced by the camera? (2 points)
 Suppose we want to store the video signal on a hard disk, and, in order to save
space, re-quantize each chrominance (Cr, Cb) signals with only 6 bits per sample. What is the minimum size of the hard disk required to store 10 minutes of video (3 points)
Q.2 The following sequence of real numbers has been obtained sampling an audio signal: 1.8, 2.2, 2.2, 3.2, 3.3, 3.3, 2.5, 2.8, 2.8, 2.8, 1.5, 1.0, 1.2, 1.2, 1.8, 2.2, 2.2, 2.2, 1.9, 2.3, 1.2, 0.2, -1.2, -1.2, -1.7, -1.1, -2.2, -1.5, -1.5, -0.7, 0.1, 0.9 Quantize this sequence by dividing the interval [-4, 4] into 32 uniformly distributed levels (place the level 0 at -3.75, the level 1 at -3.5, and so on. This should simplify your calculations).
 Write down the quantized sequence. (4 points)
 How many bits do you need to transmit it? (1 points)
Q.3 Temporal aliasing can be observed when you attempt to record a rotating wheel with a video camera. In this problem, you will analyze such effects. Assume there is a car moving at 36 km/hr and you record the car using a film, which traditionally record at 24 frames per second. The tires have a diameter of 0.4244 meters. Each tire has a white mark to gauge the speed of rotation.
 If you are watching this projected movie in a theatre, what do you perceive the rate of tire rotation to be in rotations/sec? (5 points)
 If you use your camcorder to record the movie in the theater and your camcorder is recording at half the film rate (ie 12 fps), at what rate (rotations/sec) does the tire rotate in your video recording (5 points)
Programming Part: (80 points)
This assignment will help you gain a practical understanding of Quantization and Subsampling to analyze how it affects visual media types like images and video.
We have provided you with a Microsoft Visual C++ project and a java class to display two images side by side (left – original and right – output of your program). Currently both left and right correspond to the same input image. You are free to use this display program as a start, or write your own in a language of your choice.
Input to your program will be five parameters where
 The first parameter is the name of the image, which will be provided in an 8 bit
per channel RGB format (Total 24 bits per pixel). You may assume that all images will be of the same size for this assignment, more information on the image format will be placed on the class website
 The next three parameters control the subsampling of your Y U and V spaces respectively. For sake of simplicity, we will follow the convention that subsampling occurs only along the width dimension and not the height. Each of these parameters can take on values from 1 to n for some n, 1 suggesting no sub sampling and n suggesting a sub sampling by n
 The last parameter Q controls quantization of your R, G and B values. It is a number that specifies how many different values each channel can have
To invoke your program we will compile it and run it at the command line as
YourProgram.exe C:/myDir/myImage.rgb Y U V Q
where Y U V Q are the parameters as described above. Example inputs are shown below and this should give you a fair idea about what your input parameters do and how your program will be tested.
1. YourProgram,exe image1.rgb 1 1 1 256
There are 256 values (8 bits) per R G and B, and no subsampling in the Y, U or V -> which implies that the output is the same as the input
2. YourProgram,exe image1.rgb 1 1 1 64
There are 64 values (6 bits) per R G and B and no subsampling in Y, U or V.
3. YourProgram,exe image1.rgb 1 2 2 256
There are 256 values (8bits) per R, G and B (no additional color quantization), but the U and V channels are subsampled by 2. No subsampling in the Y channels.
Now for the details - In order the display an image on a display device, the normal choice is an RGB representation. This is what the format of the input image is. However, for YUV processing reasons, you will have to convert the image in YUV space, process your subsampling and reconvert it back to RGB space to show the output to display. Here is the dataflow pipeline that illustrates all the steps.
￼￼￼1.Read Input Image
3. Process YUV subsampling
Display Input Image
￼This code is already provided to you, if you choose to make use of it
￼￼￼2. Convert to YUV space
￼The RGB to YUV with the conversion matrix is given below
￼￼Sub sample Y U and V separately according to the input parameters
￼￼￼Adjust sample values. Although samples are lost, prior to conversion to RGB all the channels have to of the same size
￼4. Adjust upsampling for display
￼￼￼5. Convert back to RGB space
Apply the inverse matrix to get the RGB data
￼￼￼￼6. Quantize RGB channels
Display Input Image
Quantize the color channels according to the input parameter and display
￼Conversion of RGB to YUV
Given R, G and B values the conversion from RGB to YUV is given by
Y U V
=
0.299 0.587 0.114 R -0.147 -0.289 0.436 G 0.615 -0.515 -0.100 B
Remember that if RGB channels are represented by n bits each, then the YUV channels are also represented by the same number of bits.
RGB values are positive, but YUV can take negative values!
Conversion of YUV to RGB
Given R, G and B values the conversion from RGB to YUV is given by
R G B
=
0.999 0.000 1.140 Y 1.000 -0.395 -0.581 U 1.000 2.032 -0.000 V
Remember that if YUV channels are represented by n bits each, then the RGB channels are also represented by the same number of bits.
YUV channel can have negative values, but RGB is always positive!
Sub sampling of YUV & processing
Sub sampling, as you know will reduce the number of samples for a channel. Eg for the input parameters
YourProgram.exe image1.rgb 1 2 2 256
In this example, the YUV image is not subsampled in Y, but by 2 in U and by 2 in V resulting in
When converting back to the RGB space, all the YUV channels have to be of the same size. However the sampling throws away samples, which have to be filled in appropriately by average the neighborhood values. For example, for the above case a local image area would look like
￼￼￼￼￼￼￼￼￼￼￼
￼￼￼￼￼￼Y11U11V11 Y12 Y13U13V13 Y14 . . . . .
￼￼￼￼￼￼￼￼￼￼￼line 1
￼￼￼￼￼￼￼Y21U21V21 Y22 Y23U23V23 Y24 . . . . .
￼￼￼￼￼￼￼￼￼￼￼￼￼￼￼￼￼￼line 2
￼￼￼￼￼￼￼￼￼￼￼Y31U31V31 Y32 Y33U33V33 Y34 . . . . .
￼￼￼￼line 3
￼￼￼￼￼￼￼Y41U41V41 Y42 Y43U43V43 Y44 . . . . .
￼￼￼￼￼￼￼￼￼￼￼￼￼￼￼￼￼line 4
￼￼￼￼￼￼￼The missing values may be filled in as
U12 = (U11 + U13)/2 V12 = (V11 + V13)/2 U14 = (U13 + U15)/2 V14 = (V13 + V15)/2 .... And so on, to get
￼￼￼￼￼￼Y11U11V11 Y12U12V12 Y13U13V13 Y14U14V14 . . . . .
￼￼￼￼￼￼￼￼￼￼line 1
￼￼￼￼￼￼￼Y21U21V21 Y22U22V22 Y23U23V23 Y24U24V24 . . . . .
￼￼￼￼￼￼￼￼￼￼￼￼￼￼￼￼￼￼line 2
￼￼￼￼￼￼￼￼￼￼￼Y31U31V31 Y32U32V32 Y33U33V33 Y34U34V34 . . . . .
￼￼￼￼line 3
￼￼￼￼￼￼￼Y41U41V41 Y42U42V42 Y43U43V43 Y44U44V44 . . . . .
￼￼￼￼￼￼￼￼￼￼￼￼￼￼￼￼line 4
￼￼￼￼￼Note the samples that you take to fill in values will change depending on the subsampling parameters. The YUV components can now be converted to RGB space.
RGB Color Quantization.
Assume that the quantization levels are uniformly distributed. Initially we have 8 bits per pixel per channel to start with. So the Q input value to your program can take on values from the range 255 – 0. For instance
 Q=256, implies 8 bits per channel or 256 possible values for each channel, so the output number of bits is same as input.
 Q=8, implies 3 bits per channel or 8 possible values which may be 0, 31, 63, 95, 127, 159, 191, 223,
 Q=64, implies 6 bits per channel or 64 possible values which may be 0, 3, 7, 11, ... 239, 243, 247, 251
 Remember Q may not necessarily be a power of 2. So design your quantization function accordingly.
What should you submit ?
 Your source code, and your project file or makefile, if any, using the submit program. Please do not submit any binaries. We will compile your program and execute our tests accordingly.
 Along with the program, also submit an electronic document (word, pdf, pagemaker etc format) using the submit program that answers the fore-mentioned analysis questions. You may use any (or all) input images for this analysis.
Analysis Question 1:
Subsampling obviously degrades the image quality. Here you are going to analyze this degradation. Assume Q is constant at 256 . For the three sub sampling parameters, vary one keeping the other two fixed and report your analysis in terms of image quality. Eg – Keep U and V constant at 1, and vary Y to find out for what value of Y does the image quality get unacceptable. Then keeping U and V constant at 1 and 2 respectively, vary Y and find out when the image quality is unacceptable. This way for various constant values of U and V, you can find the effect on the image for a varying Y. Remember the acceptability of distortion in your output images is very subjective and depends on your individual visual evaluations – but in most cases you will see a trend over variations of the parameters. You may use graphs, tables etc. to report your results. Repeat the same exercise keeping Y and U constant, but varying V, and then keeping Y and V constant but varying U.
What conclusion(s) can you draw from your analysis?
Analysis Question 2:
You will see that your analysis varies for different images. If you had to improve the quality of your results for each case above (Y varying but U, V constant) or (U varying but Y,V constant) or (V varying, but Y,U constant), what changes can you implement to your code and where can you implement them?
Can you make these changes and submit some output images with before and after the change? You may screen grab images and paste them into your document. Remember not to scale the images – that could introduce additional artifacts.
Note you do not want to submit the changed source code since we will not be able to test it. Your visual images along with explanations in your document should be fine.
