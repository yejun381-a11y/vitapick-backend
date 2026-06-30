package com.vita.vitapickBack.cscenter;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cscenter")
public class CscenterController {

	@GetMapping("/check-admin")
	public ResponseEntity<Boolean> checkAdmin(Authentication authentication) {

		if (authentication == null) {
			return ResponseEntity.ok(false);
		}

		boolean isAdmin = authentication.getAuthorities().stream()
				.anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

		return ResponseEntity.ok(isAdmin);
	}
}
