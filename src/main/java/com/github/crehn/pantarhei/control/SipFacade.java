package com.github.crehn.pantarhei.control;

import java.util.UUID;

import javax.inject.Inject;

import com.github.crehn.pantarhei.api.Sip;
import com.github.crehn.pantarhei.data.SipEntity;
import com.github.crehn.pantarhei.data.SipStore;

public class SipFacade {

	@Inject
	private SipStore store;

	public Sip getSip(UUID guid) {
		// TODO Auto-generated method stub
		return null;
	}

	public void storeSip(Sip sip) {
		store.store(toEntity(sip));
	}

	private SipEntity toEntity(Sip sip) {
		return SipEntity.builder() //
				.guid(sip.getGuid()) //
				.title(sip.getTitle()) //
				.build();
	}

}
