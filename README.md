# Camcoder
An android application that lets you take an image of handwritten code and execute it.

## Execution stages
The application follows client-server architecture.
</br></br>
![Flow](https://i.imgur.com/bPPumT8.jpg)

## Pre-processing 

### 1. Border Detection / Finding largest contour
The first step in preprocessing is the detection of the page/ board border. The user may
click an image which includes backgrounds such as surfaces of tables, which may introduce
unnecessary noise and thus hamper overall efficiency of the system. Border detection helps to
extract the region of interest. The algorithm finds all the continuous contours in the image. The
largest contour from this is chosen, which is the page border. As all the required text will be
written inside the page we can be sure that the largest continuous contour is our page border.</br>
<img src="https://i.imgur.com/WmUnFZq.jpg"  alt = "Border Detection" width = "600"/>

### 2. Rotation for inclined contours
The extracted contour may not be a perfect rectangle. So a minimum bounding rectangle is
fitted by finding the 4 corner points on the contour. The side of this contour-bounding rectangle
may be at an angle with the horizontal. Using such an image for text recognition will give
ambiguous results. Thus if there is an inclination in the detected contour the image is straightened
before further processing. The angle of the contour with the horizontal is calculated and then the
image is rotated accordingly in the opposite direction.</br>
<img src="https://i.imgur.com/Luvjfgc.jpg"  alt = "Minimum bounding rectangle" width = "600"/>
<img src="https://i.imgur.com/sbxy0Hv.jpg"  alt = "Rotated minimum bounding rectangle" width = "600"/>

### 3. Extracting region of interest after rotation
After the image is rotated we now want to extract only that region of the image which has
the page inside it. Thus the rotated image is cropped according to the minimum bounding rectangle
found in the previous step. All the text will definitely be written inside the page thus we can be
sure that the region we get after cropping contains the written text and it is our required region of
interest.</br>
<img src="https://i.imgur.com/Erxrl7K.jpg"  alt = "Cropped image after rotation" width = "600"/>

### 4. Binarization and Noise Removal
The output of the previous step now has the image which is properly oriented with all the
noisy background removed. The standard image processing techniques are now applied to this
image to make it ready for text recognition. These techniques include Binarization (using Otsu‟s
method) and dust and noise removal using erosion, smallest connected component method, etc.</br>
Deskewing text within the page:
The orientation of the page is corrected by applying all the above mentioned techniques.
But it may still be possible that the text within the page is written in a slant way. Thus as a final
step of pre-processing. the inclination of the text block with respect to the page is found. Hough
transform is used to find the orientation of the text block. If the text is written at an angle the image
is rotated accordingly in the opposite direction.</br>
<img src="https://i.imgur.com/seDW757.jpg"  alt = "Cropped image after rotation" width = "600"/>

Code for above steps is available [here](https://github.com/deveshasha/Camcoder_server/blob/master/upload/views.py) under 
```preprocess_image``` function.
