package org.magnum.mobilecloud.video;

import java.security.Principal;
import java.util.Collection;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.magnum.mobilecloud.video.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;

//import retrofit.http.GET;
//import retrofit.http.POST;
//import retrofit.http.Path;
//import retrofit.http.Query;

import com.google.common.collect.Lists;

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
	
	@Autowired
	private VideoRepository videos;
	
	@RequestMapping(value=VIDEO_SVC_PATH,method=RequestMethod.POST)
	public @ResponseBody Video addVideo(@RequestBody Video video) {
		video.setLikes(0);
		videos.save(video);
		return video;
	}

	@RequestMapping(value=VIDEO_SVC_PATH,method=RequestMethod.GET)
	public @ResponseBody Collection<Video> getVideoList() {
		return Lists.newArrayList(videos.findAll());
	}

	@RequestMapping(value=VIDEO_SVC_PATH+ "/{id}",method=RequestMethod.GET)
	public @ResponseBody Video getVideoById(@PathVariable("id") long id) {
		return videos.findOne(id);
	}
	
	@RequestMapping(value=VIDEO_TITLE_SEARCH_PATH,method=RequestMethod.GET)
	public @ResponseBody Collection<Video> findByTitle(@Param(TITLE_PARAMETER) String title) {
		return videos.findByName(title);
	}
	
	@RequestMapping(value=VIDEO_DURATION_SEARCH_PATH,method=RequestMethod.GET)
	public @ResponseBody Collection<Video> findByDurationLessThan(@Param(DURATION_PARAMETER) long duration){
		return videos.findByDurationLessThan(duration);
	}
	
	@RequestMapping(value=VIDEO_SVC_PATH + "/{id}/like",method=RequestMethod.POST)
	public void likeVideo(@PathVariable("id") long id, Principal p, HttpServletResponse response){
		Video v = videos.findOne(id);
		String username = p.getName();
		System.out.println("Test");
		if(v == null){
			response.setStatus(HttpStatus.NOT_FOUND.value());
			return;
		}
		Set<String> likesUsernames = v.getLikesUsernames();
		if(likesUsernames.contains(username)) {
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			return;
		}
		likesUsernames.add(username);
		v.setLikesUsernames(likesUsernames);
		v.setLikes(likesUsernames.size());
		videos.save(v);
		return;
	}
	
	@RequestMapping(value=VIDEO_SVC_PATH + "/{id}/unlike",method=RequestMethod.POST)
	public void unlikeVideo(@PathVariable("id") long id, Principal p, HttpServletResponse response){
		Video v = videos.findOne(id);
		String username = p.getName();
		if(v == null){
			response.setStatus(HttpStatus.NOT_FOUND.value());
			return;
		}
		Set<String> likesUsernames = v.getLikesUsernames();
		if(likesUsernames.contains(username)) {
			likesUsernames.remove(username);
			v.setLikesUsernames(likesUsernames);
			v.setLikes(likesUsernames.size());
			videos.save(v);
			return;
		} else {
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			return;
		}
	}

	@RequestMapping(value=VIDEO_SVC_PATH + "/{id}/likedby",method=RequestMethod.GET)
	public @ResponseBody Collection<String> getUsersWhoLikedVideo(@PathVariable("id") long id, HttpServletResponse response){
		Video v = videos.findOne(id);
		if(v == null){
			response.setStatus(HttpStatus.NOT_FOUND.value());
		}
		return v.getLikesUsernames();
	}

}
