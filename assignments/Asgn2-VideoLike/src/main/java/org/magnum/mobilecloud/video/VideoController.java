package org.magnum.mobilecloud.video;

import java.util.Collection;

import org.magnum.mobilecloud.video.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import retrofit.http.Path;

@Controller
public class VideoController {
	public static final String TITLE_PARAMETER = "title";
	
	public static final String DURATION_PARAMETER = "duration";

	public static final String TOKEN_PATH = "/oauth/token";

	// The path where we expect the VideoSvc to live
	public static final String VIDEO_SVC_PATH = "/video";

	// The path to search videos by title
	public static final String VIDEO_TITLE_SEARCH_PATH = VIDEO_SVC_PATH + "/search/findByName";
	
	// The path to search videos by title
	public static final String VIDEO_DURATION_SEARCH_PATH = VIDEO_SVC_PATH + "/search/findByDurationLessThan";
	
	@RequestMapping(value=VIDEO_SVC_PATH,method=RequestMethod.POST)
	public @ResponseBody Video addVideo(@RequestBody Video video) {
		return video;
	}

	@RequestMapping(value=VIDEO_SVC_PATH,method=RequestMethod.GET)
	public @ResponseBody Collection<Video> getVideoList() {
	}

	@RequestMapping(value=VIDEO_SVC_PATH+ "/{id}",method=RequestMethod.GET)
	public @ResponseBody Video getVideoById(@PathVariable("id") long id) {
	}
}
