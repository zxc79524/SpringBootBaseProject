package idv.blake.application.model.entity.security;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthRecordListData {
	private List<AuthRecordData> authRecordDatas;
	private long expiredTime;

}
