package typingapp.service;

import java.util.*;

public class RomajiNormalizationService {
	
//	private static final String VOWELS = "aiueo";
    private static final String GEMINATE_MARK = "n'";  // 撥音便マーク
    
    private final List<String> sortedKeys;
    final Map<String, List<String>> standardToVariants;
    private final Map<String, String> variantToStandard;
    
	public RomajiNormalizationService() {
	    this.standardToVariants = buildVariantMap();
	    this.variantToStandard = buildReverseMap(standardToVariants);

	    this.sortedKeys = new ArrayList<>(variantToStandard.keySet());
	    this.sortedKeys.sort((a, b) -> Integer.compare(b.length(), a.length()));
	}
	
	public static class NormalizationResult {
		public final String normalized;
		public final Set<String> candidates;
	
		public NormalizationResult(String normalized, Set<String> candidates) {
		    this.normalized = normalized;
		    this.candidates = Set.copyOf(candidates);
		}
		
		public String getNormalized() { return normalized; }
		public Set<String> getCandidates() { return Collections.unmodifiableSet(candidates); }
	}
    
	private static void add(
	        Map<String, List<String>> map,
	        String standard,
	        String... variants) {

	    map.put(standard, List.of(variants));
	}
	
    // 正規化マッピング: 標準形 → 揺れ表記
    private static final Map<String, List<String>> buildVariantMap() {

        Map<String, List<String>> map = new LinkedHashMap<>();
    			// ローマ字の揺れ → 標準形（正規形）への変換マッピング
				add(map, "ka", "ca");						//カ  
				add(map, "ku", "cu", "qu");				//ク
				add(map, "ko", "co");						//コ       
				add(map, "si", "shi", "ci");				//シ
				add(map, "se", "ce");						//セ
				add(map, "ti", "chi");					//チ
				add(map, "tu", "tsu");					//ツ
				add(map, "hu", "fu");   					//フ
				add(map, "nn", GEMINATE_MARK);     				//ン
				add(map, "wi", "whi");					//ウィ
				add(map, "we", "whe");					//ウェ           
				add(map, "qa", "kwa", "qwa");				//クァ
				add(map, "qi", "qyi", "qwi");				//クィ
				add(map, "qe", "qye", "qwe");				//クェ
				add(map, "qo", "qwo");					//クォ       
				add(map, "sha", "sya");					//シャ
				add(map, "shu", "syu");					//シュ
				add(map, "she", "sye");					//シェ
				add(map, "sho", "syo");					//ショ       
				add(map, "tya", "cha", "cya");			//チャ
				add(map, "tyi", "cyi");					//チィ
				add(map, "tyu", "chu", "cyu");			//チュ
				add(map, "tye", "che", "cye");			//チェ
				add(map, "tyo", "cho", "cyo");			//チョ                
				add(map, "fa", "fwa");					//ファ
				add(map, "fi", "fwi", "fyi");			//フィ
				add(map, "fe", "fwe", "fye");			//フェ
				add(map, "fo", "fwo");					//フォ       
				add(map, "zi", "ji");					//ジ
				add(map, "vyi", "vi");					//ヴィ
				add(map, "vye", "ve");					//ヴェ
				add(map, "ja", "jya", "zya");			//ジャ
				add(map, "jyi", "zyi");					//ジィ
				add(map, "ju", "jyu", "zyu");			//ジュ
				add(map, "je", "jye", "zye");			//ジェ
				add(map, "jo", "jyo", "zyo");			//ジョ
				add(map, "xa", "la");					//ァ
				add(map, "xi", "xyi", "li", "lyi");		//ィ
				add(map, "xu", "lu");					//ゥ
				add(map, "xe", "xye", "le", "lye");		//ェ
				add(map, "xo", "lo");					//ｫ        
				add(map, "xka", "lka");					//ヵ
				add(map, "xke", "lke");					//ヶ       
				add(map, "xya", "lya");					//ャ
				add(map, "xyu", "lyu");					//ュ
				add(map, "xyo", "lyo");					//ョ        
				add(map, "xwa", "lwa");					//ヮ      
				add(map, "xtu", "xtsu", "ltu", "ltsu");	//ッ
				add(map, "＆", "&");	// ＆
				
				return map;
    }
    
    //表記揺れで定義されているものをそのまま返す
    private static Map<String, String> buildReverseMap(Map<String, List<String>> forward) {

        Map<String, String> reverse = new HashMap<>();

        for (Map.Entry<String, List<String>> entry : forward.entrySet()) {

            String standard = entry.getKey();

            for (String variant : entry.getValue()) {
                reverse.put(variant, standard);
            }
        }

        return reverse;
    }
    
	// 表記揺れを正規化するメソッド
	public String normalize(String input){
		String result = input.toLowerCase();
		StringBuilder sb = new StringBuilder();

	    int i = 0;
		
	    while (i < result.length()) {
	        boolean matched = false;

	        for (String variant : sortedKeys) {
	            if (result.startsWith(variant, i)) {
	                sb.append(variantToStandard.get(variant));
	                i += variant.length();
	                matched = true;
	                break;
	            }
	        }

	        if (!matched) {
	            sb.append(result.charAt(i));
	            i++;
	        }
	    }

	    return sb.toString();
	} 
	
//	// --- nn の特別処理 ---
//	private static void handleDoubleN(String normalized, Set<String> candidates) {
//		
//		if (normalized.equals("nn")) {
//			
//		}
//		
//	    for (int i = 0; i < normalized.length() - 1; i++) {
//	        if (normalized.charAt(i) == 'n' && normalized.charAt(i + 1) == 'n') {
//	            // 末尾の "nn" は常に "n" に変換可能
//	            if (i + 2 >= normalized.length()) {
//	                String withSingleN = normalized.substring(0, i + 1);
//	                candidates.add(withSingleN);
//	            } 
//	            // "nn" の後ろが子音の場合のみ "n" に変換可能
//	            else {
//	                char nextChar = normalized.charAt(i + 2);
//	                if (VOWELS.indexOf(nextChar) == -1 && nextChar != 'n') {
//	                    String withSingleN = normalized.substring(0, i + 1) 
//	                                       + normalized.substring(i + 2);
//	                    candidates.add(withSingleN);
//	                }
//	            }
//	        }
//	    }
//	}

//    // --- uとiの揺れ処理 ---
//    private static void handleInitialVowels(String normalized, Set<String> candidates) {
//        
//        if (normalized.equals("i")) {
//            candidates.add("yi");
//        }
//
//        if (normalized.equals("u")) {
//            candidates.add("wu");
//            candidates.add("whu");
//        }
//    }
	
//	//ローマ字の表記揺れをまとめるメソッド
//	public NormalizationResult normalizeWithCandidates(String input){
//		String normalized = normalize(input);
//		// 表記揺れを標準化
//		Set<String> candidates = new LinkedHashSet<>();
//		
//		//cabdidatesに表記揺れを追加
//		candidates.add(normalized);
//		for (Map.Entry<String, List<String>> entry : standardToVariants.entrySet()) {
//				// さらに表記揺れが複数ある場合も検索し、追加する
//			String standard = entry.getKey();
//			
//			// 表記揺れの元となっている文字を追加
//			if (normalized.contains(standard)) {
//				// 文字に表記揺れを含む可能性のある文字があれば
//				for (String variant : entry.getValue()) {
//					
//					// 表記揺れの候補を入れる
//                    String candidate = normalized.replace(standard, variant);
//                    candidates.add(candidate);
//                    // 全てに表記揺れの変換を追加する(sisigami→shishigami)
//				} 
//			}
//		}
//		
//		handleDoubleN(normalized, candidates); // nnが含まれているかで処理をする
//			
//		handleInitialVowels(normalized, candidates); // a~oを含まれているかで処理をする
//		
//		// normalizedとcanidatesを返すために関数で返している	
//		return new NormalizationResult(normalized, candidates);
//	}
	
    private void addSpecialVariants(String normalized, Set<String> variants) {
		switch (normalized) {
			case "nn":
				variants.add("n");
				break;
			case "i":
				variants.add("yi");
				break;
			case "u":
				variants.add("wu");
				variants.add("whu");
				break;
			default:
				break;
		}
	}   
    
	public List<String> getVariants(String normalized) {

	    LinkedHashSet<String> variants = new LinkedHashSet<>();

	    variants.add(normalized);

	    List<String> list = standardToVariants.get(normalized);
	    if (list != null) {
	        variants.addAll(list);
	    }

	    addSpecialVariants(normalized, variants);

	    return new ArrayList<>(variants);
	}
    
}