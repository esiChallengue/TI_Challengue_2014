/**
 * Adobe Edge: symbol definitions
 */
(function($, Edge, compId){
//images folder
var im='images/';

var fonts = {};


var resources = [
];
var symbols = {
"stage": {
   version: "2.0.0",
   minimumCompatibleVersion: "2.0.0",
   build: "2.0.0.250",
   baseState: "Base State",
   initialState: "Base State",
   gpuAccelerate: false,
   resizeInstances: false,
   content: {
         dom: [
         {
            id:'RoundRect4',
            type:'rect',
            rect:['12px','10px','347px','215px','auto','auto'],
            borderRadius:["10px","10px","10px","10px"],
            fill:["rgba(169,213,245,1.00)"],
            stroke:[1,"rgb(0, 96, 243)","solid"]
         },
         {
            id:'RoundRect2',
            type:'rect',
            rect:['0px','17px','22px','215px','auto','auto'],
            borderRadius:["10px","10px","10px","10px"],
            fill:["rgba(192,192,192,1)"],
            stroke:[1,"rgba(0,96,243,1.00)","solid"]
         },
         {
            id:'RoundRect2Copy',
            type:'rect',
            rect:['346px','17px','22px','215px','auto','auto'],
            borderRadius:["10px","10px","10px","10px"],
            fill:["rgba(192,192,192,1)"],
            stroke:[1,"rgba(0,96,243,1.00)","solid"]
         },
         {
            id:'RoundRect2Copy2',
            type:'rect',
            rect:['174px','36px','22px','368px','auto','auto'],
            borderRadius:["10px","10px","10px","10px"],
            fill:["rgba(192,192,192,1)"],
            stroke:[1,"rgba(0,96,243,1.00)","solid"]
         },
         {
            id:'RoundRect3',
            type:'rect',
            rect:['63px','75px','47px','111px','auto','auto'],
            borderRadius:["10px","10px","10px","10px"],
            fill:["rgba(208,208,208,1.00)"],
            stroke:[1,"rgba(32,65,95,1.00)","solid"]
         },
         {
            id:'RoundRect2Copy3',
            type:'rect',
            rect:['175px','-176px','17px','368px','auto','auto'],
            borderRadius:["10px","10px","10px","10px"],
            fill:["rgba(192,192,192,1)"],
            stroke:[1,"rgba(0,96,243,1.00)","solid"]
         }],
         symbolInstances: [

         ]
      },
   states: {
      "Base State": {
         "${_RoundRect3}": [
            ["color", "background-color", 'rgba(208,208,208,1.00)'],
            ["color", "border-color", 'rgba(32,65,95,1.00)'],
            ["transform", "rotateZ", '0deg']
         ],
         "${_RoundRect2Copy}": [
            ["style", "top", '17px'],
            ["style", "left", '346px'],
            ["color", "background-color", 'rgba(72,150,227,1)'],
            ["color", "border-color", 'rgb(0, 96, 243)'],
            ["style", "height", '215px'],
            ["style", "border-style", 'solid'],
            ["style", "border-width", '1px'],
            ["style", "width", '22px']
         ],
         "${_RoundRect2}": [
            ["style", "top", '17px'],
            ["color", "background-color", 'rgba(72,150,227,1.00)'],
            ["style", "border-style", 'solid'],
            ["style", "height", '215px'],
            ["color", "border-color", 'rgba(0,96,243,1.00)'],
            ["style", "border-width", '1px'],
            ["style", "width", '22px']
         ],
         "${_Stage}": [
            ["color", "background-color", 'rgba(255,255,255,1.00)'],
            ["style", "overflow", 'hidden'],
            ["style", "height", '232px'],
            ["style", "width", '370px']
         ],
         "${_RoundRect2Copy3}": [
            ["color", "background-color", 'rgba(72,150,227,1)'],
            ["transform", "rotateZ", '90deg'],
            ["style", "border-style", 'solid'],
            ["style", "border-width", '1px'],
            ["style", "width", '17px'],
            ["style", "top", '-176px'],
            ["style", "height", '368px'],
            ["color", "border-color", 'rgb(0, 96, 243)'],
            ["style", "left", '175px']
         ],
         "${_RoundRect2Copy2}": [
            ["color", "background-color", 'rgba(72,150,227,1)'],
            ["transform", "rotateZ", '90deg'],
            ["style", "border-style", 'solid'],
            ["style", "border-width", '1px'],
            ["style", "width", '22px'],
            ["style", "top", '36px'],
            ["style", "height", '368px'],
            ["color", "border-color", 'rgb(0, 96, 243)'],
            ["style", "left", '174px']
         ],
         "${_RoundRect4}": [
            ["color", "background-color", 'rgba(169,213,245,1.00)']
         ]
      }
   },
   timelines: {
      "Default Timeline": {
         fromState: "Base State",
         toState: "",
         duration: 3000,
         autoPlay: true,
         timeline: [
            { id: "eid13", tween: [ "transform", "${_RoundRect3}", "rotateZ", '35deg', { fromValue: '0deg'}], position: 0, duration: 1000 },
            { id: "eid14", tween: [ "transform", "${_RoundRect3}", "rotateZ", '90deg', { fromValue: '35deg'}], position: 1000, duration: 1000 },
            { id: "eid15", tween: [ "transform", "${_RoundRect3}", "rotateZ", '180deg', { fromValue: '90deg'}], position: 2000, duration: 1000 }         ]
      }
   }
}
};


Edge.registerCompositionDefn(compId, symbols, fonts, resources);

/**
 * Adobe Edge DOM Ready Event Handler
 */
$(window).ready(function() {
     Edge.launchComposition(compId);
});
})(jQuery, AdobeEdge, "EDGE-38908700");
