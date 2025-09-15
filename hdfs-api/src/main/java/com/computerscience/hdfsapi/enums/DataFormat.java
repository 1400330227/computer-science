package com.computerscience.hdfsapi.enums;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 数据格式类型枚举
 * 包含文本、视频、音频等格式
 * @date 2024年
 */
public enum DataFormat {

    // 文本格式
    TXT("Plain Text File", ".txt", "最基础的纯文本文件，无任何格式", "文本"),
    RTF("Rich Text Format", ".rtf", "富文本格式，支持粗体、斜体等基本格式", "文本"),
    DOC("Microsoft Word Document (老版本)", ".doc", "Microsoft Word 97-2003文档", "文本"),
    DOCX("Microsoft Word Open XML Document", ".docx", "Microsoft Word 2007及之后版本的文档", "文本"),
    ODT("OpenDocument Text Document", ".odt", "LibreOffice, OpenOffice等开源办公软件的文本格式", "文本"),
    PDF("Portable Document Format", ".pdf", "便携式文档格式，用于保留所有格式的跨平台文档", "文本"),
    HTML("HyperText Markup Language", ".html/.htm", "网页文件，包含网页的结构和内容", "文本"),
    XML("eXtensible Markup Language", ".xml", "可扩展标记语言，用于存储和传输数据", "文本"),
    JSON("JavaScript Object Notation", ".json", "轻量级的数据交换格式，易于人阅读和编写", "文本"),
    CSV("Comma-Separated Values", ".csv", "逗号分隔值文件，用于存储表格数据（如Excel）", "文本"),
    TEX("LaTeX Source Document", ".tex", "LaTeX排版系统的源文件", "文本"),
    LOG("Log File", ".log", "日志文件，记录系统或软件的运行事件", "文本"),
    MARKDOWN("Markdown Documentation", ".md/.markdown", "轻量级标记语言文件，常用於README文件等", "文本"),
    INI("Initialization File", ".ini", "初始化配置文件，用于保存程序的设置", "文本"),
    YAML("YAML Ain't Markup Language", ".yaml/.yml", "另一种人性化的数据序列化标准，常用於配置文件", "文本"),
    SRT("SubRip Subtitle", ".srt", "字幕文件，纯文本格式存储视频字幕和时间码", "文本"),

    // Excel表格格式
    XLS("Microsoft Excel Spreadsheet (老版本)", ".xls", "Microsoft Excel 97-2003电子表格文档", "文本"),
    XLSX("Microsoft Excel Open XML Spreadsheet", ".xlsx", "Microsoft Excel 2007及之后版本的电子表格文档", "文本"),
    ODS("OpenDocument Spreadsheet", ".ods", "LibreOffice Calc、OpenOffice Calc等开源办公软件的电子表格格式", "文本"),

    // PowerPoint演示文稿格式
    PPT("Microsoft PowerPoint Presentation (老版本)", ".ppt", "Microsoft PowerPoint 97-2003演示文稿文档", "文本"),
    PPTX("Microsoft PowerPoint Open XML Presentation", ".pptx", "Microsoft PowerPoint 2007及之后版本的演示文稿文档", "文本"),
    ODP("OpenDocument Presentation", ".odp", "LibreOffice Impress、OpenOffice Impress等开源办公软件的演示文稿格式", "文本"),

    // 视频格式
    MP4("MPEG-4 Part 14", ".mp4", "最常见的格式。通用性强，兼容性好，通常包含H.264视频和AAC音频。", "视频"),
    AVI("Audio Video Interleave", ".avi", "微软开发的旧容器格式，体积通常较大，但兼容性极好。", "视频"),
    MKV("Matroska Video", ".mkv", "开源容器格式，功能强大，可封装几乎所有编码的视频、音频、字幕轨道。", "视频"),
    MOV("QuickTime File Format", ".mov", "苹果公司开发的格式，常用于专业视频编辑和macOS生态系统。", "视频"),
    WMV("Windows Media Video", ".wmv", "微软开发的视频格式，曾常用于网络流媒体，现在较少见。", "视频"),
    FLV("Flash Video", ".flv", "Adobe Flash的视频格式，曾是网络视频的主流，现已逐渐淘汰。", "视频"),
    WEBM("Web Media", ".webm", "开源且专为网络设计的格式，通常包含VP8/VP9/AV1视频和Vorbis/Opus音频。", "视频"),
    M4V("iTunes Video File", ".m4v", "苹果iTunes使用的视频格式，是MP4的一种变体，可能包含DRM版权保护。", "视频"),
    MPEG("Moving Picture Experts Group", ".mpeg/.mpg", "早期的MPEG压缩标准视频格式。", "视频"),
    TS("MPEG Transport Stream", ".ts", "传输流格式，常用于数字广播和录制实时视频流（如卫星电视）。", "视频"),
    M2TS("BDAV MPEG-2 Transport Stream", ".m2ts", "蓝光光盘使用的视频格式，基于TS格式。", "视频"),
    THREE_GP("3GPP Multimedia File", ".3gp", "为移动设备设计的低带宽格式，常见于老式功能手机录制的视频。", "视频"),
    VOB("DVD Video Object", ".vob", "DVD光盘中的视频文件，包含MPEG-2视频、音频、字幕和菜单。", "视频"),
    RM("RealMedia / RealMedia Variable Bitrate", ".rm/.rmvb", "RealNetworks公司的格式，rmvb支持可变比特率，曾流行于网络下载。", "视频"),
    ASF("Advanced Systems Format", ".asf", "微软开发的流媒体容器，WMV和WMA常封装在其中。", "视频"),
    OGV("Ogg Video", ".ogv", "Xiph.Org基金会开源的容器格式，通常包含Theora视频和Vorbis音频。", "视频"),
    DIVX("DivX Media Format", ".divx", "基于MPEG-4 Part 2编码的一种特定品牌格式。", "视频"),

    // 音频格式
    MP3("MPEG-1 Audio Layer III", ".mp3", "最普及的音频格式。体积小，兼容性极好，但牺牲了部分音质。", "音频"),
    FLAC("Free Lossless Audio Codec", ".flac", "开源的无损格式。音质完美等同于CD，体积约为CD的一半。", "音频"),
    WAV("Waveform Audio File Format", ".wav", "由微软和IBM开发，保留了完整的音频数据，音质最好，但体积巨大。", "音频"),
    AAC("Advanced Audio Coding", ".aac", "效率高于MP3，音质更好或体积更小。是MP4视频的标准音频格式。", "音频"),
    WMA("Windows Media Audio", ".wma", "微软开发的格式，有有损（WMA）和无损（WMA Lossless）两种版本。", "音频"),
    M4A("MPEG-4 Audio", ".m4a", "苹果常用的音频格式，通常包含AAC编码（有损）或ALAC编码（无损）的音频。", "音频"),
    ALAC("Apple Lossless Audio Codec", ".alac", "苹果开发的无损格式，音质等同于FLAC，但兼容性不如FLAC。", "音频"),
    OGG("Ogg Vorbis", ".ogg", "开源的有损格式，是MP3和AAC的竞争对手，音质优于同码率的MP3。", "音频"),
    OPUS("Opus Audio Codec", ".opus", "非常现代的开源格式，专为交互式网络应用设计，在低比特率下音质卓越。", "音频"),
    AIFF("Audio Interchange File Format", ".aiff", "苹果开发的未压缩格式，类似于WAV，常用于macOS专业音频领域。", "音频"),
    APE("Monkey's Audio", ".ape", "压缩率很高的无损格式，但编码解码速度较慢，不如FLAC普及。", "音频"),
    DSD("Direct Stream Digital", ".dsd/.dff/.dsf", "用于Super Audio CD (SACD) 的高分辨率音频格式，体积非常大。", "音频"),
    MIDI("Musical Instrument Digital Interface", ".mid/.midi", "不是音频，而是记录演奏指令（按了哪个键、力度多大），需要音源合成声音。", "音频"),
    AMR("Adaptive Multi-Rate", ".amr", "专为语音优化，常见于手机录音文件。", "音频"),
    AC3("Audio Codec 3", ".ac3", "Dolby Digital环绕声编码，常用于DVD和蓝光光盘。", "音频"),
    DTS("Digital Theater Systems", ".dts", "另一种常见的环绕声编码，是杜比数字的竞争对手。", "音频"),

    // 图像格式
    JPG("Joint Photographic Experts Group", ".jpg/.jpeg", "最常见的图像格式。通过压缩大幅减小文件体积，但会永久丢失部分数据。适合存储颜色丰富的照片，但不适合保存logo、线稿等需要清晰边缘的图片。", "图像"),
    PNG("Portable Network Graphics", ".png", "支持透明度（透明通道）。压缩效率高且无损，适合保存logo、图标、网页图形、截图等需要清晰边缘或透明背景的图片。通常体积比JPG大。", "图像"),
    GIF("Graphics Interchange Format", ".gif", "支持动画和简单的透明度（只有完全透明或不透明，没有半透明）。最多只支持256色，适合颜色简单的图形、动画表情包。", "图像"),
    BMP("Bitmap", ".bmp", "Windows系统中的标准位图格式。几乎不进行压缩，保留了完整的图像数据，因此画质最好但体积非常大，现在已不常用。", "图像"),
    TIFF("Tagged Image File Format", ".tiff/.tif", "高质量专业格式。支持图层、多种压缩方案，常用于印刷出版、扫描、专业摄影等领域，以保证最高质量。", "图像"),
    WEBP("Web Picture", ".webp", "由Google开发，专为Web设计。在相同质量下，体积比JPEG和PNG更小，支持透明度和动画。正在成为Web图像的新标准。", "图像"),
    SVG("Scalable Vector Graphics", ".svg", "基于XML的矢量格式。图像由数学公式定义，因此可以无限放大而不失真。非常适合logo、图标、图表等需要多种尺寸显示的图形。", "图像"),
    EPS("Encapsulated PostScript", ".eps", "一种老式的矢量格式，常用于印刷和Adobe Illustrator软件。", "图像"),
    RAW("Raw Image Format", ".raw", "\"数字底片\"。是相机传感器捕获的原始、未处理的数据，保留了最大的图像信息，为后期编辑提供了极大灵活性。不同相机厂商有各自的RAW格式（如.CR2, .NEF, .ARW）。", "图像"),
    ICO("Windows Icon", ".ico", "专门用于存储计算机图标的格式，包含多个尺寸和色深的一个或多个图像。", "图像"),
    HEIC("High Efficiency Image File Format", ".heic/.heif", "由MPEG开发的新一代格式，采用先进的压缩技术，在相同画质下文件大小比JPEG小约50%。是苹果设备（iPhone、iPad）的默认照片格式。", "图像"),
    PSD("Photoshop Document", ".psd", "Adobe Photoshop的源文件格式，支持图层、蒙版、通道等所有编辑信息，用于图像编辑和设计。", "图像"),
    AI("Adobe Illustrator Artwork", ".ai", "Adobe Illustrator的源文件格式，用于存储矢量图形设计的原始数据。", "图像"),
    INDD("InDesign Document", ".indd", "Adobe InDesign的源文件格式，用于排版设计。", "图像");

    private final String fullName;
    private final String extension;
    private final String description;
    private final String category;

    DataFormat(String fullName, String extension, String description, String category) {
        this.fullName = fullName;
        this.extension = extension;
        this.description = description;
        this.category = category;
    }

    @JsonCreator
    public static DataFormat getEnum(String extension) {
        for (DataFormat format : DataFormat.values()) {
            if (format.getExtension().equals(extension)) {
                return format;
            }
        }
        return null;
    }

    @JsonValue
    public String getExtension() {
        return extension;
    }

    public String getFullName() {
        return fullName;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    /**
     * 根据文件扩展名获取数据格式
     * @param fileName 文件名
     * @return 对应的数据格式，如果未找到返回null
     */
    public static DataFormat getByFileName(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return null;
        }
        
        String lowerFileName = fileName.toLowerCase();
        for (DataFormat format : DataFormat.values()) {
            String[] extensions = format.getExtension().split("/");
            for (String ext : extensions) {
                if (lowerFileName.endsWith(ext.toLowerCase())) {
                    return format;
                }
            }
        }
        return null;
    }

    /**
     * 根据类别获取所有格式
     * @param category 类别（TEXT, VIDEO, AUDIO）
     * @return 该类别下的所有格式
     */
    public static DataFormat[] getByCategory(String category) {
        return java.util.Arrays.stream(DataFormat.values())
                .filter(format -> format.getCategory().equals(category))
                .toArray(DataFormat[]::new);
    }
}