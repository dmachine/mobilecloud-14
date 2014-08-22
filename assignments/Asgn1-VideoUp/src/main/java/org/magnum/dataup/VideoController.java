package org.magnum.dataup;


import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.multipart.MultipartFile;
import org.magnum.dataup.model.Video;
import org.magnum.dataup.model.VideoStatus;

import java.util.*;
import java.io.*;
import java.util.concurrent.atomic.AtomicLong;
import javax.servlet.http.*;

@Controller
public class VideoController {
	public static final String DATA_PARAMETER = "data";

	public static final String ID_PARAMETER = "id";

	public static final String VIDEO_SVC_PATH = "/video";
	
	public static final String VIDEO_DATA_PATH = VIDEO_SVC_PATH + "/{id}/data";

	private Map<Long, Video> videos = new HashMap<Long, Video>();
    private VideoFileManager videoDataMgr;

	@RequestMapping(value=VIDEO_SVC_PATH,method=RequestMethod.GET)
	public @ResponseBody Collection<Video> getVideoList() {
		return videos.values();
	}

	@RequestMapping(value=VIDEO_SVC_PATH,method=RequestMethod.POST)
	public @ResponseBody Video addVideo(@RequestBody Video video) {
		save(video);
		video.setDataUrl(getDataUrl(video.getId()));
		return video;
	}

	@RequestMapping(value=VIDEO_DATA_PATH,method=RequestMethod.POST)
	public @ResponseBody VideoStatus setVideoData(@PathVariable(ID_PARAMETER) long id, @RequestParam(DATA_PARAMETER) MultipartFile videoData, HttpServletResponse response) throws IOException{
		Video v = videos.get(id);
		videoDataMgr = VideoFileManager.get();
		if(v != null) {
			saveSomeVideo(v, videoData);
		} else{
			response.setStatus(HttpStatus.NOT_FOUND.value());
		}
		VideoStatus vs = new VideoStatus(VideoStatus.VideoState.READY);
		return vs;
	}

	@RequestMapping(value=VIDEO_DATA_PATH,method=RequestMethod.GET)
	public void getData(@PathVariable(ID_PARAMETER) long id, HttpServletResponse response) throws IOException {
		Video v = videos.get(id);
		videoDataMgr = VideoFileManager.get();
		if(v != null) {
			serveSomeVideo(v, response);
		} else{
			response.setStatus(HttpStatus.NOT_FOUND.value());
		}
	}

	// Methods provided by readme
	private static final AtomicLong currentId = new AtomicLong(0L);
    public Video save(Video entity) {
        checkAndSetId(entity);
        videos.put(entity.getId(), entity);
        return entity;
    }
    private void checkAndSetId(Video entity) {
        if(entity.getId() == 0){
            entity.setId(currentId.incrementAndGet());
        }
    }
    private String getDataUrl(long videoId){
        String url = getUrlBaseForLocalServer() + "/video/" + videoId + "/data";
        return url;
    }
    private String getUrlBaseForLocalServer() {
        HttpServletRequest request =
            ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String base =
           "http://"+request.getServerName()
           + ((request.getServerPort() != 80) ? ":"+request.getServerPort() : "");
        return base;
    }
    public void saveSomeVideo(Video v, MultipartFile videoData) throws IOException {
        videoDataMgr.saveVideoData(v, videoData.getInputStream());
    }
    public void serveSomeVideo(Video v, HttpServletResponse response) throws IOException {
        // Of course, you would need to send some headers, etc. to the
        // client too!
        //  ...
        videoDataMgr.copyVideoData(v, response.getOutputStream());
   }
}
