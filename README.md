MagickExportPlugin for Chunky 2.4+
==================================

Export renderings to many new formats using ImageMagick or GraphicsMagick command line tool.

## Supported lossless formats:

- OpenEXR
- PFM - Portable Float Map
- PNG using 8/16 bit per channel

## Requirements

- Chunky 2.4 or higher

## Installation

1. Download the [newest version from the releases](./releases/latest) and put it in `.chunky/plugins/`
2. Start the Chunky launcher and ensure that you have the newest release installed (2.4-SNAPSHOT or higher)
3. Enable MagickExportPlugin in the plugin manager
4. Start chunky and check if Magick can be found

## Troubleshooting

- Output looks weird or has color artifacts: try to change the output endianness in the expert settings
