package com.javas.analysis;

import com.javas.analysis.dto.News;
import com.javas.analysis.repository.NewsRepository;
import java.util.ArrayList;
import java.util.List;
import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.KomoranResult;
import kr.co.shineware.util.common.model.Pair;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.apache.commons.lang3.StringUtils;

@Slf4j
@DataMongoTest
public class AnalysisTest {

  @Autowired
  NewsRepository newsRepository;

  @Test
  void T1() {
    Komoran komoran = new Komoran(DEFAULT_MODEL.FULL);
    KomoranResult result = komoran.analyze("기성용 측 '녹취 파일' 공개에…폭로자 측 \"악마의 편집한 것\""
        + "축구선수 기성용의 '초등학교 시절 성폭행 의혹'을 제기한 이들의 법률대리인 박지훈 변호사가 기성용 측에 \"악의적인 증거 조작을 통한 언론 플레이를 중단하라\"고 촉구했다.\n"
        + "\n"
        + "법무법인 현의 박지훈 변호사는 18일 \"기성용 선수의 법률대리인은 본 사안을 진흙탕 싸움으로 몰아가고 있다\"며 \"재판이 아닌 '언론플레이'와 '여론재판'으로 본 사건의 진실을 가리자는 기성용 선수 측 법률대리인의 주장은 변호사로서 매우 부적절한 주장\"이라고 지적했다.\n"
        + "\n"
        + "박 변호사는 기성용 측이 진실을 밝혀줄 '확실한 증거'를 공개하라고 요구한 것에 대해 \"MBC 'PD수첩'을 통해 '기성용 선수 자신이 측근을 통해 과거 행위에 대한 과오를 인정하며 사과 의사를 표시하는 한편 피해자를 회유하고 피해자에게 지속적인 오보 압박을 가했다는 사실'이 담긴 녹취 파일이 공개됐다\"며 \"그럼에도 기성용 선수 측 법률대리인은 아직까지도 '당장 증거를 내놓아 보아라'는 식의 요구를 반복하고 있다\"고 말했다.\n"
        + "\n"
        + "지난 17일 기성용 측이 공개한 피해자 D의 육성 파일에 대해서는 \"기성용 선수 측 법률대리인은 녹음파일들을 이리저리 잘라 내고 붙여가며 '악마의 편집'을 해 언론에 배포했다\"고 주장했다.\n"
        + "\n"
        + "박 변호사는 \"녹음 파일에는 기성용 선수의 성폭행 사실을 폭로한 피해자들이 기성용 선수 측으로부터 회유와 압박을 받아 괴로워하고 고민하는 과정에서 내뱉은 여러 가지 말들이 여과 없이 담겨져 있다\"며 \"기성용 선수 측 법률대리인은 피해자 말의 앞뒤를 잘라내고 이어붙여 날조한 자료로 언론플레이를 펼치며 국민을 선동하고 있다\"고 했다.\n"
        + "\n"
        + "그러면서 \"법정에서 법률과 증거를 갖고 진실을 규명하자\"고 덧붙였다.\n"
        + "\n"
        + "앞서 지난 16일 방송된 MBC 'PD수첩-우리들의 일그러진 영웅' 편에서는 기성용을 포함해 스포츠 스타들의 학교 폭력 제보자 증언이 공개됐다.\n"
        + "\n"
        + "기성용은 지난달 24일 축구 선수 출신 A씨와 B씨가 2000년 전남 모 초등학교 축구부에서 생활하던 시절 기성용과 또 다른 선배에게 구강성교를 강요당했다고 폭로하면서 성폭행 의혹에 휩싸인 바 있다.\n"
        + "\n"
        + "기성용에게 성폭력을 당했다고 밝힌 이들은 PD수첩에 출연해 \"초등학교 시절 당했던 피해로 고통받고 있다\"고 주장하며 확실한 증거를 법정에서 공개하겠다고 했다.\n"
        + "\n"
        + "방송 다음날인 지난 17일 기성용의 법률대리인인 법무법인 서평의 송상엽 변호사는 \"'PD수첩'은 국민들에게 무엇이 진실인가에 대한 편향된 시각을 제공했다\"고 주장하며 \"진실을 밝혀준다는 '확실한 증거'를 즉시 공개하라\"고 촉구했다.\n"
        + "\n"
        + "또한 기성용 측 송 변호사는 피해자 D의 육성 파일을 공개하며 오는 26일 안으로 법적 조치를 취할 것이라고 밝혔다. 이날 송 변호사가 공개한 녹취 파일에는 피해자 D가 '기성용이 성폭력 가해자가 아니다'라는 취지로 말하는 내용이 담겼다.");
    List<Pair<String, String>> sentences = result.getList();
    for (Pair<String,String> token : sentences) {
      System.out.println(token.getFirst()+" "+token.getSecond());
    }
  }

  @Test
  void T2() {
    News news = new News();
    news = newsRepository.findFirstByUri("https://news.naver.com/main/read.naver?mode=LS2D&mid=shm&sid1=101&sid2=771&oid=277&aid=0004954195");
    log.info(news.toString());

    ArrayList<String> beforeAnalysis = new ArrayList<>();
    beforeAnalysis.add(news.getTitle());
    beforeAnalysis.add(news.getContent());

    if (!StringUtils.isNotEmpty(news.getSummary())) {
      beforeAnalysis.add(news.getSummary());
    }

    Komoran komoran = new Komoran(DEFAULT_MODEL.FULL);

    for (String sentences : beforeAnalysis) {
      KomoranResult result = komoran.analyze(sentences);
      List<Pair<String, String>> list = result.getList();
      log.info(list.size()+"");
      for (Pair<String, String> pair : list) {

        log.info(pair.getFirst() + " " + pair.getSecond());

      }
    }

  }
}