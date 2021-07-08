# RScan

Product identification is one of the tasks a blind person must deal with every day. Various products have their physical characteristics, so it is possible to distinguish a chocolate from a box of drugs simply by hand.\
But what flavor of chocolate and what type of drug one holds in their hand is mostly distinguishable only by the visual design, which can't be recognized naturally.

There are OCR apps out there to help blind people read the cases and tell what they're holding, but there is another problem. When it comes to text, many products have a lot of information on their cases, marking the most important by separating them, using a bigger font and a different color so sighted person can tell right away what are they looking at. However, this is not anything special for OCr engines, which simply put all information together in a one bunch of text, making it hard to get to the important parts through minor details in various languages.

## RScan solution

To allow quick and accurate recognition of products, RScan uses the marking all of them contain - barcodes.

Currently, four barcode standards are supported:
* [EAN-13,](https://en.wikipedia.org/wiki/International_Article_Number) European Article Number, a 13 digit number used primarily in Europe, globally specific for each product
* [EAN-8,](https://en.wikipedia.org/wiki/EAN-8) a smaller, 8 digit version of EAN-13, which can also be used for local products reference
* [UPC-A,](https://en.wikipedia.org/wiki/Universal_Product_Code) Universal Product Code, a 12 digit number used primarily in USA and Canada with the same purpose as EAN-13
* [UPC-E,](https://en.wikipedia.org/wiki/Universal_Product_Code#UPC-E) a smaller, 6 digit version of UPC-A

Unfortunately, while barcodes mark any product quite reliably, there are no online databases providing product descriptions for them. There are few databases trying to collect them, but they're far from complete even for EAN-13.

RScan thus takes a different approach. Instead of relying on a third-party service to do the job, it lets the user to collect their own database, providing handy tools to help in this process. The idea is, that most of the products used by an average person are the same, being it your favourite flavor of chocolate or your preferred drug for flu.\
Over time, your local database should cover everything you normally use, making third-party services practically needless.

Note that RScan is not a general purpose barcode scanner, it's aimed primarily for direct product identification. Therefore, supported barcode types are limited to those usable for this purpose and formats like QR codes along with their sharing functionalities aren't and most likely won't be supported.

## Installation

The easiest way to get RScan up and running is to install it wia [F-Droid,](https://fdroid.org/) the open source app repository for Android.

Alternatively, you can also compile and run the program from source. Once you have JDK 11 and Android SDK in place, all you need to do to compile RScan is:\
```./gradlew assembleDebug```\
or release, according to your needs.

## Usage

### main screen

After opening the app and confirming permissions, realtime scanning will start automatically. You can aim your phone on the product you want to identify and search for a barcode. The app will beep and announce the type, if one is found. If the scanned barcode is present in your local database, instead of the barcode type, its description will be announced.

All scanned barcodes will appear in the scanning results list, where you can describe them. You can clear this list with the Clear list button in the bottom bar.

You can also activate your flashlight to get better results using the Use flashlight toggle. There is currently no automatic mode, so you'll have to either turn it on or off.

You can also import and export your barcode database called BarcodeCache, using the respective buttons on the bottom bar. Currently these operations support only reading from and writing to clipboard, it's a temporary solution until I get more familiar with the new Android Q scoped file operations.

### Barcode identification screen

After tapping on a scanned barcode, the barcode identification screen will appear. The most important part is the bottom bar with an EditText and the Save button. Here you can set the description of the product and save it to your database.

There is a list of suggested descriptions in the center of the screen, taken from a [DuckDuckGo](https://duckduckgo.com/) search. Clicking on one of them will copy it to the EditText.

The last important thing is the word selector in the left part of the bottom bar. This feature makes use of the fact that search results from DuckDuckGo or other engines often give suitable descriptions, just with some noise on the end like the name of the store. Using the word selector, you can select just X words from the start, making it easy to use already existing descriptions without a need to use the keyboard.\
Each time you change the count (except changing it to zero), the last included word will be spoken, so you can make the selection quickly and efficiently.\
The value of 0 means that the selector is disabled and the entered description will be used without any further modifications.

### Warning!

The suggested descriptions, while helpful and often quite accurate, are just searches of the barcode on DuckDuckGo. There is no guarantee that a true match was found and the description really matches the product.\
In cases where a reliable identification is necessary, like with drugs, please take additional steps to verify the correctness of the suggestions either by searching the code manually, using an OCR program or asking a sighted person.\
The author of the program, as stated by [The GNU General Public License version 3,](https://github.com/RastislavKish/RScan/blob/main/LICENSE) is in no way responsible for any damage caused directly or indirectly by the app or its users, see the license for more details.

## Scanning tips

### Camera manipulation and environment

First of all, make sure you have enough light around. Activating your phone's flashlight should help, but in a complete darkness, this may not be enough.

Hold your phone approximately 30CM above the plane you're scanning and try to hold it as parallel with it as possible. Try leaning it a bit in various directions if you're not sure, or you can place it camera down on to the surface and raise, so you have a better control.

The rotation of the barcode in the camera field doesn't really matter, it can be scanned horizontally, vertically, upside-down or even diagonally. However, what matters is the part of object's surface you have in your camera's view, as more surface you cover, the bigger chance is for you to catch the barcode.

Therefore, it is recommended that you scan primarily in orthogonal positions, as they usually cover most of the potential space where a barcode could occur.

### Finding a barcode

Scanning a barcode is nice, but how do you find it on the box in the first place?

You can of course scan every square centimeter and eventually find it, but it can take some time which you can save by following few simple guidelines.

First of all, try to imagine yourself as a seller and the places you'd consider for placing a barcode. Such a place must meet two basic requirements:

* It must be out of sight. No seller wants ugly bars on their fancy product mask, so you'll typically find them either on the product's bottom or sides, but not on the top.
* It must be easily accessible, so market employees can scan it quickly and thus serve more customers. That means it typically won't be really hidden, just out of sight.

Also, most barcodes are usually placed orthogonally so they're easier to focus.

Here are few types of product you'll likely encounter and the places where barcodes usually occur:

* Chocolate, try searching on the bottom side either on the ends or in parallel with the middle band.
* Bottles, they usually have a band of paper on them like a paper ring. Find the place where one end of the paper is sticked to another (creating the ring), the barcode will most likely be there as the area is not suitable for placing graphics nor text. If your bottle has an etiquette sticked on it instead, scan it directly.
* Chips and other delicacies in plastic bags, they usually have a band similar to the one of chocolates, except that it doesn't necessarily need to be that wide (it can be more like a line). Try scanning along it in parallel, it should do the trick. Also, if you find etiquettes sticked on the bag, try scanning them as well, they could contain the barcode.
* Boxes, they usually have barcodes either on their bottom or the sides. Note that bottom here means the side, which is usually in contact with the surface on which the product is placed in its most stable position. It's usually the largest side, which is either not used at all, or contains additional information about the product.

Note that these guidelines are not laws, in theory, a seller can place barcodes wherever they want. These advices will in most cases help you get to the result faster, but if you can't progress with them, it might be worth checking all of the object's surface so you don't miss anything.

## Assets

The program currently contains one asset, the sound of a barcode scanner beep. You can find it [here,](https://freesound.org/people/zerolagtime/sounds/144418/) all rights belong to its authors.\
It is distributed under the terms of its original license ([Creative Commons 0](https://creativecommons.org/publicdomain/zero/1.0/)).

