var width = 320;    // We will scale the photo width to this
var height = 0;     // This will be computed based on the input stream
var streaming = false;
var video = null;
var canvas = null;
var photo = null;
var startbutton = null;
var allowMakePhoto = true;
var CONTENT_TYPE = 'image/png';
var paid;

function stopVideoRecording() {
    allowMakePhoto = false;
}


function startVideoRecording(p) {
    __log("Starting video recording", []);
    paid = p;
    allowMakePhoto = true;
    video = document.getElementById('video');
    video.setAttribute('width', "128");
    canvas = document.getElementById('canvas');
    startbutton = document.getElementById('startbutton');


    navigator.mediaDevices.getUserMedia({video: true, audio: false})
        .then(function (stream) {
            video.srcObject = stream;
            video.play();
        })
        .catch(function (err) {
            __log("An error occurred: " + err);
        });

    video.addEventListener('canplay', function (ev) {
        if (!streaming) {
            height = video.videoHeight / (video.videoWidth / width);

            // Firefox currently has a bug where the height can't be read from
            // the video, so we will make assumptions if this happens.

            if (isNaN(height)) {
                height = width / (4 / 3);
            }

            video.setAttribute('width', "128");
            streaming = true;
        }
    }, false);

    var timerId = setTimeout(function tick() {
        if (allowMakePhoto) {
            takepicture();
            timerId = setTimeout(tick, 30000); // (*)
            __log("Make photo");
        }
    }, 30000);

    __log("Video recording started");

}

function takepicture() {
    __log("Take picture");
    var context = canvas.getContext('2d');
    if (width && height) {
        canvas.width = width;
        canvas.height = height;
        context.drawImage(video, 0, 0, width, height);

        canvas.toBlob(function (blob) {
            url = URL.createObjectURL(blob);
            uploadPicture(blob);

        });
    }
    __log("Picture has been created");
}

function uploadPicture(blob) {

    var filename = new Date().toISOString();

    var xhr = new XMLHttpRequest();
    xhr.onload = function (e) {
        if (this.readyState === 4) {
            __log("Server returned: ", e.target.responseText);
        }
        __log("Server returned: ", e);
    };

    console.log("Prepare data for upload ", paid);

    var fd = new FormData();
    fd.append("picture_data", blob, filename);
    fd.append("picture_ct", CONTENT_TYPE);
    fd.append("pid", paid);
    xhr.open("POST", "upload/photo", true);
    xhr.send(fd);

    __log("phote uploaded ", paid);


}


//helper function
function __log(e, data) {
    console.info(e, data);
}