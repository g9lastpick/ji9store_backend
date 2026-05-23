package com.jjsoft.pos.service.admin;





import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationConstraint.OperatorType;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellBorder.BorderSide;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;


/** poi excel download
 *  client params 정보

	options :{
		sheet_name : "sheet1",  //시트명 , 시트 보호 암호로 셋팅됨
		sheet_title : "title name",
		sheet_protected : true , // default true
		
		// title_s_row :0 , title_e_row : 0 이면 title 안나옴
		// title_s_row :1 , title_e_row : 2 이면 title 첫번째줄에  A2 row에 title_merge_cnt 만큼 머지되어 메시지 나옴 그리고 첫번째 row에  confideltial 나옴
		title_s_row : 1 , 
		title_e_row : 2 , // 데이터 나오기 전 메시지 end row => 생략가능 생략하면 title 안나옴
		title_merge_cnt : 9 , // 타이틀 나올시에 row merge 될 셀 갯수 => 보통 엑셀에 나올 컬럼 수 만큼 셋팅
		confidential_cell_pos : 9 , // confidential 셀 위치 
		is_confidential : false , // default tuue
		fix_row : 4 , //틀고정4행		
		fix_col : 3 , //틀고정3열 row col 둘다 존재해야 셋팅됨
		body_font : "맑은고딕",
		body_font_site : 12,
		
		header_color: "240,240,240" ==> 생략가능 rgb롤 셋팅해야함. 컴마로붆래야함. 값 이상할경우 240,240,240으롤 셋팅됨
		header_font : "맑은고딕",
		header_font_size : 12,
		header_bold : true ,
		
		data_empty_row : 50, // 빈 row 필요시 정의
		is_auto_size : true , // 컬럼 사이즈 자동조정 - 데이터 기반임.
		
		//해더 위에 가이드 문구 생성 - 맨 윗줄로도 생성가능.
		 guide_list : ["db에있는 값만 넣으세요","",""],
		 guide_disp_first : true,
		 
		 merge_group : ["id", "productId", "productNm" ],// merge 할 기준 컬럼명 배열 , 머지 그룹에는 머ㅗ지셀에 있는 컬럼명을 포함하고 있어야함.
		 merge_cell : ["id",  "productNm"], 
		 //그리드 하단 합계정보 셋팅
		 summary_info : [
		 	{ // ex
		 		summaryRow : [
		 			{
		 				excel_merge_row_s : 0 , excel_merge_row : 4 , cell_color : "240,240,240" , value : "합계" , excel_col_type : "string" , excel_align : "right"
		 			},
		 			{
		 				excel_merge_row_s : 5, excel_merge_row : 8 , cell_color : "240,240,240" , value : "SUM(H4:H9)" , excel_col_type : "formula" , excel_align : "right" , excel_format: "#,###"
		 			},
		 		]
		 	},
		 	{
		 		summaryRow : [
		 			{
		 				excel_merge_row_s : 0 , excel_merge_row : 4 , cell_color : "240,240,240" , value : "합계" , excel_col_type : "string" , excel_align : "right"
		 			},
		 			{
		 				excel_merge_row_s : 5, excel_merge_row : 8 , cell_color : "240,240,240" , value : "SUM(H4:H9)" , excel_col_type : "formula" , excel_align : "right" , excel_format: "#,###"
		 			},
		 		]
		 	},
		 ],
		 
				
	},
	// header 셋팅 방법 - 헤더 셋팅 방법 -  column 필수값 만약 헤더가 부모일 경우 name으로 셋팅.
	  { name : "아이디"  , column : "id" 
	  , excel_view : true // excel 표현 여부
	  , excel_modify : true , // excel 수정 여부
	  , excel_format : "#,###" //data format 엑셀과 동일하게 먹음
	  , excel_align : "left" // 셀 데이터 정렬
	  // excel_col_type  정의 
	  // string , number(양의 정수만 입력가능) 
	  // decimal : 입력재한 때문에 셋팅값이 좀 있음 (excel_col_pos 필수값 , 값은 column위치 대문자 사용 예)excel_col_pos: "H" ) 
	  //           excel_vld : "decimalPoint2"  => 이렇게 셋팅할 경우 소수점 2자리수까지 입력가능함.
	  // date : 날짜 지정 -> excel_format 입력 안되면 default : yyyy-mm-dd 적용
	  // formula : 수식 입력시 => 수식은 = 빼고 입력해야함. ex) A1+C1 or SUM(A1:C1)
	  // excel_formula_obj : { is_new_formula : true, // 해당 옵션이 true이면 value값 입력안되고 아래 셋팅 조합으로 입력됨
	  //                       is_fix_ref : false , //해당옵션이 true이면 고정 참조
	  //                       direction : "H" , // V ,H 가로 세로형 셋팅 , 가로형만 지원예정
	  //                       type : "plus" , 
	  //                       cell_array : [A,B,C,D,E] , // 수식 셍 A1+B1+C1+D1+E1 이ㅣ렇게 셋팅됨 ,해당값이 있으면 s_cell , e_cell 무시됨  
	  //                       s_cell : "A",  
	  //                       e_cell : "C", // SUM(A1:C1) 이렇게 셋팅됨 
	  // }            
	  //, excel_col_type : "string"
	  //, excel_memo : "" ,  
	  //, excel_merge_row_s : 해더 로우 merge시 start index
	  //, excel_merge_row_e : 해더 로우 merge시 end index
	  //, excel_merge_col : 해더 col merge시 갯수
	  //, excel_width : 해당 값이 없으면 columns에 있는 값 참조 default 200
	  //headers = 배열임  ex) 부모 헤더에는 column 키가 없음 name만 존재
	  // [{
	  //   name: "유저정보" , // or headerText: "유저정보"
	  //   excel_merge_row_s : 1 // 머지할 헤더 로우 갯수 시작 인덱스 
	  //   excel_merge_row_e : 3 // 머지할 헤더 로우 갯수 끝 인덱스 
	  //   excel_view : true //해당 값이 true여야 엑셀에 표현됨  
	  //   derection : "horizontal" //셀 전개 방향  
	  //   groupShowMode : "expend",
	  //   expandable : true ,
	  //   expanded : true ,
	  //   items : [
	  //      {column :"id"   , name="아이디", excel_view:true , excel_align: "right",  excel_col_type:"string" , excel_modify: false , excel_format:"" , excel_memo:"수정불가한 필드" , groupShowMode: "expand"}
	  //      {column :"name" , name="이름" , excel_view:true , excel_align: "right",  excel_col_type:"string" , excel_modify: false , excel_format:"" , }
	  //      {column :"age"  , name="나이" , excel_view:true , excel_align: "right",  excel_col_type:"string" , excel_modify: false , excel_format:"" , }
	  //   ],
	  // },
	  // { column : "salesId" , excel_view: true , excel_merge_col : 2 , excel_modify : false },
	  //  ]  
	  // excel down 요청
	  // params = { datalist , columns , headers : header , options , fileName : ""}
	  // let res = await axios.post("url" , params , {responseType: "blob" , showProg: isLoging } )
	  // if(res.status === 200){
	  // 	const blob = await res.data
	  // 	const url  = window.URL.createObjectURL(blob)
	  // 	const a    = document.createElement("a")
	  // 	a.href = url
	  // 	a.download = fileName + "xlsx"
	  // 	document.body.appendChild(a)
	  // 	a.click()
	  // 	document.body.removeChild(a)
	  // }
	  }
	columns :[
		{ name :"id" //header  의 column과 일치해야함. 
		, width : "200"
		, lookupDisplay : true 
		, lookupData : [{text:"방문",value:"vi"},{text:"예약",value:"re"}] /// select box 나오는 부분
		}
	],
	headers : [
	
	],
	dataList : [
		컬럼과 맞는 json data 배열
		ex : {col1: "" , col2:""}
	],
	fileName : "",

 * 
 * */
@Service
@Slf4j
public class ExcelDownService {
	
	
	private String headerFont = "";
	private String bodyFont = "";
	private Integer defaultColWidth= 20;
	private Integer addWidth= 50;
	
	private List<String> comboColNameList;
	private List<String> vldColNameList;//입력제한 column 설정 리스트
	
	private XSSFWorkbook workbook;
	private Map<String , XSSFCellStyle> cellStyleContainer;
	

	@SuppressWarnings("unchecked")
	public ResponseEntity<byte[]> excelDownload( HashMap<String ,Object> reqParamMap){//Jwt principal ,
		
		cellStyleContainer = new HashMap<>();
		
		List<Map<String , Object>> dataList = objToList(reqParamMap.get("dataList"));
		List<Map<String , Object>> columns  = objToList(reqParamMap.get("columns"));
		List<Map<String , Object>> headers  = objToList(reqParamMap.get("headers"));
		Map<String , Object>       options  = objToMap (reqParamMap.get("options"));
		
		comboColNameList  = new ArrayList<>();
		vldColNameList    = new ArrayList<>();
		
//		String userId = principal.getClaimAsString("userId");
		
		String     title  = objToString(reqParamMap.get("sheet_title"));
		String sheetName  = objToString(options.get("sheet_name")).equals("") ? " sheet1" :  objToString(options.get("sheet_name"));
		
		int  title_s_row  = objToInt(options.get("title_s_row"));
		int  title_e_row  = objToInt(options.get("title_e_row"));
		int titleMergeCnt = objToInt(options.get("title_merge_cnt"));
		int  dataEmptyCnt = objToInt(options.get("data_empty_cnt"));
		int        fixRow = objToInt(options.get("fix_row"));
		int        fixCol = objToInt(options.get("fix_col"));
		
		List   dataMergeGrp = objToList(options.get("merge_group"));
		List  dataMergeCell = objToList(options.get("merge_cell"));
		List    summaryInfo = objToList(options.get("summary_info"));
		List      guideList = objToList(options.get("guide_list"));
		
		boolean isAutoSize = objToBool(options.get("is_auto_size"));
		boolean sheetProtected = options.get("sheet_protected") == null? true : objToBool(options.get("sheet_protected"));//default true
		boolean guideDispFirst = objToBool(options.get("guide_disp_first"));
		
		
		//기본폰트 써야함
		headerFont = objToString(options.get("header_font")).equals("")?"맑은 고딕" : objToString(options.get("header_font"));
		bodyFont   = objToString(options.get("body_font")).equals("")  ?"맑은 고딕" : objToString(options.get("body_font"));
		
		int confidential_Cell_pos = objToInt(options.get("confidential_Cell_pos"));
		
		/** rowindex는 현재 excel의 row point 여ㅑㄱ할을 하는 index로 생성된 로우 바로 아래를 가리킴 */
		int rowIndex = 0;
		int dataMergeSIdx = 0;//data merge시 시작 인덱스 -> 헤더 다음 인덱스 저장해둠
		workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet(sheetName);
		
		if(sheetProtected) {
			sheet.lockFormatRows(false);
			sheet.lockInsertRows(false);
			sheet.lockDeleteRows(false);
			sheet.lockFormatColumns(false);//컬럼 넓이 조정
			sheet.protectSheet(sheetName);
		}
		sheet.setDisplayGridlines(false);//눈금선 삭제
		
		//해더 위에 가이드 문구 생성
		if(guideList.size() > 0 && guideDispFirst) {
			rowIndex = createGuide(sheet ,guideList , rowIndex);
		}
		
		if(title_e_row != -1) {
			rowIndex = createHeaderTitle(sheet , title , rowIndex , title_s_row , title_e_row , titleMergeCnt , confidential_Cell_pos,
					options , guideDispFirst);
		}
		
		//해더 위에 가이드 문구 생성 - 왜 두번 하지??
		if(guideList.size() > 0 && guideDispFirst) {
			rowIndex = createGuide(sheet ,guideList , rowIndex);
		}
		
		rowIndex = createHeader(sheet , columns , headers , options , rowIndex);
		
		dataMergeSIdx = rowIndex;
		
		List<Map<String ,Object>> changeHeaders = new ArrayList<>();
		changeHeader(headers , changeHeaders);
		
		if(dataList.size() > 0) {
			rowIndex = createBody(sheet ,columns , dataList , dataEmptyCnt , options , changeHeaders , rowIndex);
		}
		if(dataEmptyCnt > 0) {
			rowIndex = createBodyEmpty (sheet , dataEmptyCnt , columns , options , changeHeaders , rowIndex);
		}
		
		if(dataList.size() > 0 && dataMergeGrp.size() > 0 && dataMergeCell.size() >0) {
			createDataMerge(sheet , columns ,dataList , options ,changeHeaders , dataMergeSIdx);
		}
		
		//하단 summary info
		if(dataList.size() > 0 && summaryInfo.size() > 0) {
			rowIndex = createSummary(sheet , summaryInfo , columns , options , changeHeaders , rowIndex);
		}
		
		//틀고정
		if(fixRow >=0 && fixCol >= 0) {
			sheet.createFreezePane(fixCol, fixRow);
		}
		
		//컬럼 사이즈 자동 조정
		if(isAutoSize) {
			try {
				int headerLoop = 0;
				for(Map<String , Object> header : changeHeaders) {
					if(header == null) continue;
					//header(layout)에서 부모인지 아닌지 판단
					boolean isExcel = objToBool(header.get("excel_view"));
					if(!isExcel) {//자식들 중에 excel 표현 정의된 컬럼만 표현함
						continue;
					}
					String colNm = objToString(header.get("column"));
					int maxLen = objToInt((header.get("maxLen")));
					maxLen = maxLen == 0 ? 5 : maxLen+5;
					if(maxLen > 255) maxLen = 100;
					sheet.setColumnWidth(headerLoop, maxLen*256);
					headerLoop++;
				}
			} catch (Exception e) {
				// TODO: handle exception
				log.error("ERROR ERROR ERROR ERROR ERROR  excel auto size ERROR ERROR ERROR ERROR ");
				e.printStackTrace();
			}
		}
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			workbook.write(outputStream);
			workbook.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		HttpHeaders headersResponse = new HttpHeaders();
		headersResponse.add("Content-Disposition","attachment; filename=excelDown.xlsx");
		headersResponse.add("Content-Type","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		
		
		return new ResponseEntity<>(outputStream.toByteArray(), headersResponse , HttpStatus.OK);
	}
	
	
	/**가이드 문구 생성 */
	public int createGuide(XSSFSheet sheet , List<String> guideList , int startRowIndex) {
		
		int rowIndex = startRowIndex;
		int colIndex = 0;
		
		XSSFWorkbook workbook = sheet.getWorkbook();
		XSSFCellStyle style   = (XSSFCellStyle)workbook.createCellStyle();
		XSSFColor borderColor = new XSSFColor(
			    new java.awt.Color(240, 240, 240),
			    new DefaultIndexedColorMap()
			);
		Font font             = workbook.createFont();
		Row row               = sheet.createRow(rowIndex);
		
		font.setFontName(bodyFont);
		font.setColor(IndexedColors.GREY_25_PERCENT.getIndex());
		style.setFont(font);
		
		style.setAlignment(HorizontalAlignment.LEFT);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		
		style.setBorderColor(BorderSide.LEFT, borderColor);
		style.setBorderColor(BorderSide.RIGHT, borderColor);
		style.setBorderColor(BorderSide.TOP, borderColor);
		
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);
		style.setBorderBottom(BorderStyle.THIN);
		
		DataFormat format = workbook.createDataFormat();
		style.setDataFormat(format.getFormat("@"));
		style.setLocked(true);
		style.setWrapText(true);//data 개행처리
		
		//가이드 텍스트 data 생성
		for(String guideTxt : guideList) {
			Cell dataCell = row.createCell(colIndex);
			dataCell.setCellValue(guideTxt);
			dataCell.setCellStyle(style);
			colIndex++;
		}
		
		
		return rowIndex+1;
	}
	
	private void changeHeader(List<Map<String,Object>> headers , List<Map<String,Object>> resultList) {
	
		if(headers == null || headers.size() == 0) return ;
		for(Map<String ,Object> header : headers) {
			
			boolean isExcel = objToBool(header.get("excel_view"));
			if(!isExcel) {//자식들 중에 excel 표현 정의된 컬럼만 표현함
				continue;
			}
			Map<String ,Object> map = new HashMap<>();
			if(!objToString(header.get("name")).equals("")) {
				changeHeader(objToList(header.get("items")), resultList);
			}else {
				if(objToBool(header.get("excel_view"))) {
					resultList.add(header);
				}
			}
		}
	}
	
	/** 해더 정보 위에 메시지 생성
	 * */
	public int createHeaderTitle(XSSFSheet sheet , String msg , int rowIndex , int sIdx , int eIdx ,
			int msgMergeCnt , int confidentialCell , Map<String , Object> options, boolean guideDispFirst) {
		int resultIdx = rowIndex;
		
		for (int i = rowIndex; i < eIdx; i++) {
			Row row = sheet.createRow(i);
			resultIdx++;
			if(i ==0 || (guideDispFirst && i == 1) ) {
				Cell cell = row.createCell(confidentialCell);
				cell.setCellValue("confidential");
				headerCellStyle(cell , "confidential" , options );
			}
			else if(i+1 == eIdx && msgMergeCnt > 0) {
				Cell cell = row.createCell(0);
				cell.setCellValue(msg);
				sheet.addMergedRegion(new CellRangeAddress(i,i,0,msgMergeCnt));//데이터 입력 후 셀 만큼 머지
				headerCellStyle(cell, "title" , options);
				row.setHeight((short)700);
			}
		}
		return resultIdx;
	}
	
	private Map<String ,Object> findColumnInfo(String column , List<Map<String,Object>> columns){
		
		Map<String , Object> opt = columns.stream().filter(x->((String)x.get("name")).equals(column)).findFirst().orElse(null);
		if(opt == null) return new HashMap<>();
		return opt;
	}
	
	/** 해더정보생성 */
	public int createHeader(XSSFSheet sheet ,List<Map<String,Object>> columns , List<Map<String,Object>> headers,
			Map<String,Object> options , int startRowIndex) {
		int rowIndex = startRowIndex;
		int colIndex = 0;
		List<Map<String,Object>> childRows = new ArrayList<>();
		
		Row headerRow = sheet.createRow(rowIndex);
		for(Map<String ,Object> header : headers) {
			if(header == null ) continue;
			
			boolean isParent = objToString(header.get("column")).equals("")? true: false;
			boolean isexcel  = objToBool(header.get("excel_view"));
			if(!isexcel) continue;//자식들 중 excel 표현 정의된 컬럼만 표현함.
			
			Map<String ,Object> columnInfo = findColumnInfo(objToString(header.get("column")), columns);
			String headerTitle = objToString(header.get("name")).equals("")?objToString(header.get("headerText")) : objToString(header.get("name"));
			headerTitle = headerTitle.equals("") ? "layout 셋팅 확인: " + header.get("column") : headerTitle;
			
			Cell cell = headerRow.getCell(colIndex) == null ? headerRow.createCell(colIndex) : headerRow.getCell(colIndex);
			cell.setCellValue(headerTitle);
			
			if(!isParent) 
			{
				int width = objToInt(columnInfo.get("excel_width")); 
				width = width > 0 ? width : objToInt(columnInfo.get("width"));
				width = width > 0 ? width : defaultColWidth;
				sheet.setColumnWidth(colIndex, width*addWidth);
				
				int mLen = objToInt(header.get("maxLen"));
				header.put("maxLen", Math.max(mLen, headerTitle.length()));
			}
			
			if(header.containsKey("items")) 
			{//다단 헤더 생성
				createChildHeader(sheet , childRows , columns , objToList(header.get("items")) , options , rowIndex , 0 , colIndex);
				
				int mergeSCnt = objToInt(header.get("excel_merge_row_s"));
				int mergeECnt = objToInt(header.get("excel_merge_row_e"));
				//부모헤더 머지
				for(int i = (mergeSCnt+1) ; i <= mergeECnt ; i++) 
				{
					Cell tempCell = headerRow.getCell(i) == null ? headerRow.createCell(i) : headerRow.getCell(i);
					headerCellStyle(tempCell, "header" , options);
				}
				sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex , mergeSCnt , mergeECnt));
				colIndex = mergeECnt+1;
			}
			else
			{//첫번째 컬럼 생성
				int colMergeCnt = objToInt(header.get("excel_merge_col"));
				if(colMergeCnt >0) {
					try {
						int endRow = rowIndex + colMergeCnt;
						for(int jj = rowIndex ; jj < endRow; jj++) {
							Row cRow = sheet.getRow(jj) == null? sheet.createRow(jj) : sheet.getRow(jj);
							Cell tempCell = cRow.getCell(colIndex) == null ? cRow.createCell(colIndex) : cRow.getCell(colIndex);
							headerCellStyle(tempCell , "header" , options);
						}
					} catch (Exception e) {
						// TODO: handle exception
					}
					sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex+colMergeCnt-1 , colIndex , colIndex));
				}
				headerCellStyle(cell , "header" , options);
				colIndex++;
			}
		}
		
		//row생성된 만큼 증가 후 리턴
		if(childRows.size() > 0) {
			rowIndex = rowIndex + childRows.size() + 1;
		}else {
			rowIndex++;
		}
		
		return rowIndex;
	}
	
	public void createChildHeader(XSSFSheet sheet ,List<Map<String,Object>> rows,List<Map<String,Object>> columns , 
			List<Map<String,Object>> childHeaders,Map<String,Object> options , 
			int rowIndex , int childIndex , int columnIdx) {
		
		Map<String,Object> rowMap = null;
		Row  headerRow = null;
		int childStartRowIdx = rowIndex+1;
		int tempChildIndex = childIndex;
		
		if(rows.size() > 0) {
			rowMap = rows.stream().filter(x->((Row)x.get("row")).getRowNum() == childStartRowIdx).findAny().orElse(null);
		}
		if(rowMap == null) 
		{//child row 정보 초기화
			headerRow = sheet.getRow(childStartRowIdx) == null? sheet.createRow(childStartRowIdx) : sheet.getRow(childStartRowIdx);
			rowMap = new HashMap<>();
			rowMap.put("row",headerRow);
			rowMap.put("colIndex",columnIdx);
			rows.add(rowMap);
		}else {
			headerRow = (Row)rowMap.get("row");
			rowMap.put("colIndex", columnIdx);
		}
		
		for(Map<String ,Object> header : childHeaders) {
			
			if(header == null ) continue;
			
			boolean isParent = objToString(header.get("colums")).equals("")? true: false;
			boolean isexcel  = objToBool(header.get("excel_view"));
			if(!isexcel) continue;//자식들 중 excel 표현 정의된 컬럼만 표현함.
			
			Map<String ,Object> columnInfo = findColumnInfo(objToString(header.get("column")), columns);
			String headerTitle = objToString(header.get("name")).equals("")?objToString(header.get("headerText")) : objToString(header.get("name"));
			headerTitle = headerTitle.equals("") ? "layout 셋팅 확인: " + header.get("column") : headerTitle;
			
			int colIndex = objToInt(rowMap.get("colIndex"));
			
			Cell cell = headerRow.getCell(colIndex) == null ? headerRow.createCell(colIndex) : headerRow.getCell(colIndex);
			cell.setCellValue(headerTitle);
			
			if(!isParent) 
			{
				int width = objToInt(columnInfo.get("excel_width"));
				width = width > 0 ? width : objToInt(columnInfo.get("width"));
				width = width > 0 ? width : defaultColWidth;
				sheet.setColumnWidth(colIndex, width*addWidth);
				
				int mLen = objToInt(header.get("maxLen"));
				header.put("maxLen", Math.max(mLen, headerTitle.length()));
			}
			
			if(header.containsKey("items")) 
			{//다단 헤더 생성
				tempChildIndex = tempChildIndex+1; 
				createChildHeader(sheet , rows , columns , objToList(header.get("items")) , options , childStartRowIdx , tempChildIndex , colIndex);
				
				int mergeSCnt = objToInt(header.get("excel_merge_row_s"));
				int mergeECnt = objToInt(header.get("excel_merge_row_e"));
				
				if(mergeSCnt < mergeECnt) {
					//부모헤더 머지
					for(int i = (mergeSCnt+1) ; i <= mergeECnt ; i++) 
					{
						Cell tempCell = headerRow.getCell(i) == null ? headerRow.createCell(i) : headerRow.getCell(i);
						headerCellStyle(tempCell, "header" , options);
					}
					sheet.addMergedRegion(new CellRangeAddress(childStartRowIdx, childStartRowIdx , mergeSCnt , mergeECnt));
					colIndex = mergeECnt+1;
				}
			}
			else
			{//첫번째 컬럼 생성
				int colMergeCnt = objToInt(header.get("excel_merge_col"));
				if(colMergeCnt >0) {
					try 
					{//나의 하위 셀들 생성해서 라인 먹여줌
						final Row conRow = headerRow;
						List<Map<String,Object>> filterRows =
								rows.stream().filter(x->((Row)x.get("row")).getRowNum() > conRow.getRowNum())
								             .collect(Collectors.toList());
						int curRowIdx = headerRow.getRowNum();
						int endRow = curRowIdx + colMergeCnt;
						for(int jj = curRowIdx ; jj < endRow; jj++) {
							Row cRow = sheet.getRow(jj) == null? sheet.createRow(jj) : sheet.getRow(jj);
							Cell tempCell = cRow.getCell(colIndex) == null ? cRow.createCell(colIndex) : cRow.getCell(colIndex);
							headerCellStyle(tempCell , "header" , options);
						}
					} catch (Exception e) {
						// TODO: handle exception
					}
					sheet.addMergedRegion(new CellRangeAddress(childStartRowIdx, childStartRowIdx+colMergeCnt-1 , colIndex , colIndex));
				}
				headerCellStyle(cell , "header" , options);
				colIndex++;
			}
			rowMap.put("colIndex", colIndex);
		}
	}
	
	/** excel 본문 영역 생성 */
	public int createBody(XSSFSheet sheet , List<Map<String,Object>> columns , List<Map<String,Object>> dataList , 
			int dataEmptyCnt , Map<String,Object> options , List<Map<String,Object>> changeHeaders , int startRowIndex) {
		
		int rowIndex = startRowIndex;
		
		for(Map<String ,Object> rowData : dataList)
		{
			Row dataRow = sheet.createRow(rowIndex);
			int colIndex = 0;
			
			for(Map<String ,Object> header : changeHeaders) 
			{
				String colName   = objToString(header.get("column"));
				String colType   = objToString(header.get("excel_col_type"));
				String colformat = objToString(header.get("excel_format"));
				boolean isExcel  = objToBool(header.get("excel_view"));
				
				if(!isExcel) continue;
				
				Map<String,Object> colInfo = findColumnInfo(colName, columns);
				boolean isCombo = objToBool(header.get("lookupDisplay"));
				int endRowIndex = dataList.size() + startRowIndex -1 + dataEmptyCnt;
				
				//콤보박스 설정
				if(isCombo) {
					if(!comboColNameList.contains(colName)) {
						//콤보 생성
						List<Map<String, Object>> comboList = objToList(colInfo.get("lookupData"));
						createComboToHiddenSheet(sheet , colName , startRowIndex ,endRowIndex , colIndex ,comboList, header);
						comboColNameList.add(colName);
					}
				}
				
				//입력 제한 설정
				if(!vldColNameList.contains(colName)) {
					vldColNameList.add(colName);
					createValidationToCell(sheet  , startRowIndex ,endRowIndex , colIndex , header);
				}
				
				String data = objToString(rowData.get(colName));
				Cell dataCell = dataRow.createCell(colIndex);
				bodyCellStyle(dataCell , header , colInfo);
				
				if(colType.toUpperCase().equals("FORMULA")) {
					header.put("maxLen", 15);
				}else {
					int mLen = objToInt(header.get("maxLen"));
					header.put("maxLen", Math.max(mLen, data.length()));
				}
				
				//data type은 data 변환해서 입ㅀ력해야 포멧 먹음
				if(colType.toUpperCase().equals("DATE")) {
					try {
						dataCell.setCellType(CellType.NUMERIC);
						if(!data.isEmpty() && !colformat.isEmpty()) {
							SimpleDateFormat formatter = new SimpleDateFormat(colformat);
							Date date = formatter.parse(data);
							dataCell.setCellValue(date);
						}else {
							dataCell.setCellValue(data);
						}
					} catch (Exception e) {
						// TODO: handle exception
						dataCell.setCellValue(data);
					}
				}
				else if(colType.toUpperCase().equals("FORMULA")) {
					dataCell.setCellType(CellType.FORMULA);
					if(!data.isEmpty()) dataCell.setCellFormula(data);
				}
				else if(colType.toUpperCase().equals("NUMBER")) {
					dataCell.setCellType(CellType.NUMERIC);
					dataCell.setCellValue(data);
				}
				else if(colType.toUpperCase().equals("DECIMAL")) {
					dataCell.setCellType(CellType.NUMERIC);
				    dataCell.setCellValue(data);
				}else {
					dataCell.setCellValue(data);
				}
				
				//메모 삽입
				String excelMemo = objToString(header.get("excel_memo"));
				if(!excelMemo.isEmpty()) {
					createCommentToCell(dataCell , excelMemo);
				}
				colIndex++;
			}
			rowIndex++;
		}
		
		return rowIndex;
	}
	
	
	/** excel 본문 영역 생성 */
	public int createBodyEmpty(XSSFSheet sheet , int dataEmptyCnt , List<Map<String,Object>> columns , 
			 Map<String,Object> options , List<Map<String,Object>> changeHeaders , int startRowIndex) {
		
		int rowIndex = startRowIndex;
		
		for(int i = 0; i < dataEmptyCnt ; i++)
		{
			Row dataRow = sheet.createRow(rowIndex);
			int colIndex = 0;
			
			for(Map<String ,Object> header : changeHeaders) 
			{
				String colName   = objToString(header.get("column"));
				boolean isExcel  = objToBool(header.get("excel_view"));
				
				if(!isExcel) continue;
				
				Map<String,Object> colInfo = findColumnInfo(colName, columns);
				boolean isCombo = objToBool(header.get("lookupDisplay"));
				int endRowIndex = dataEmptyCnt + startRowIndex -1 ;
				
				//콤보박스 설정
				if(isCombo) {
					if(!comboColNameList.contains(colName)) {
						//콤보 생성
						List<Map<String, Object>> comboList = objToList(colInfo.get("lookupData"));
						createComboToSheet(sheet  , startRowIndex ,dataEmptyCnt+startRowIndex-1 , colIndex ,comboList, header);
						comboColNameList.add(colName);
					}
				}
				
				//입력 제한 설정
				if(!vldColNameList.contains(colName)) {
					vldColNameList.add(colName);
					createValidationToCell(sheet  , startRowIndex ,endRowIndex , colIndex , header);
				}
				
				Cell dataCell = dataRow.createCell(colIndex);
				dataCell.setCellValue("");
				bodyCellStyle(dataCell , header , colInfo);
				
				
				//메모 삽입
				String excelMemo = objToString(header.get("excel_memo"));
				if(!excelMemo.isEmpty()) {
					createCommentToCell(dataCell , excelMemo);
				}
				colIndex++;
			}
			rowIndex++;
		}
		
		return rowIndex;
	}
	
	private int createSummary(XSSFSheet sheet , List<Map<String,Object>> summaryInfo , List<Map<String,Object>> columns , 
			 Map<String,Object> options , List<Map<String,Object>> changeHeaders , int startRowIndex) {
		int rowIndex = startRowIndex;
		
		try {
			for(Map<String,Object> summaryMap : summaryInfo) 
			{
				List<Map<String,Object>> tList = objToList(summaryMap.get("summaryRow"));
				if(tList.size() > 0) {
					Row dataRow = sheet.createRow(rowIndex);
					for(Map<String,Object> map: tList) 
					{
						int sMergePos = objToInt(map.get("excel_merge_row_s"));
						int eMergePos = objToInt(map.get("excel_merge_row_e"));
						String colType = objToString(map.get("excel_col_type"));
						String align = objToString(map.get("excel_align"));
						String format = objToString(map.get("excel_format"));
						String color = objToString(map.get("color"));
						String value = objToString(map.get("value"));
						
						Map<String,Object> header = new HashMap<>();
						header.put("excel_col_type", colType);
						header.put("excel_modify", false);
						header.put("excel_format", format);
						header.put("excel_align", align);
						
						for(int i = sMergePos; i <= eMergePos ; i++) 
						{
							Cell cell = dataRow.createCell(i);
							bodyCellStyle(cell,header , null);
							
							if(colType.toUpperCase().equals("STRING")) {
								cell.setCellType(CellType.STRING);
								cell.setCellValue(value);
							}
							else if(colType.toUpperCase().equals("FORMULA")) {
								cell.setCellType(CellType.FORMULA);
								cell.setCellFormula(value);
							}
							else {
								cell.setCellType(CellType.STRING);
								cell.setCellValue(value);
							}
						}
						sheet.addMergedRegion(new CellRangeAddress(rowIndex,rowIndex,sMergePos, eMergePos));
					}
					rowIndex++;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
		return rowIndex;
	}
	
	private void createDataMerge(XSSFSheet sheet , List<Map<String,Object>> columns ,List<Map<String,Object>> datas , 
			 Map<String,Object> options , List<Map<String,Object>> changeHeaders , int startRowIndex) {
		
		try {
			List<String> dataMergeGrp  = objToList(options.get("merge_group"));
			List<String> dataMergeCell = objToList(options.get("merge_cell"));
			
			int rowIndex = startRowIndex;
			Map<String , List<Map<String, Object>>> mergeMap = new HashMap<>();
			int groupIdx = 0;
			String pColName= null;
			/*merge 할 영역 셋팅*/
			for(Map<String ,Object> header : changeHeaders) {
				String colName = objToString(header.get("column"));
				if(!dataMergeGrp.contains(colName)) {
					continue;
				}
				
				setMergeIdx(mergeMap, datas , pColName , colName , groupIdx);
				groupIdx++;
				pColName = colName;
			}
			
			//실제 셀 별 머지
			for(String mergeColumn : dataMergeCell) 
			{
				List<Map<String,Object>> mergeList = mergeMap.get(mergeColumn);
				for(Map<String,Object> dataMergeMap : mergeList) 
				{
					int sPos = objToInt(dataMergeMap.get("s_pos")) + rowIndex;
					int ePos = objToInt(dataMergeMap.get("e_pos")) + rowIndex;
					
					List<String> colList = changeHeaders.stream().map(x->objToString(x.get("column"))).collect(Collectors.toList());
					int colIndex = colList.indexOf(mergeColumn);
					if(ePos - sPos > 0) {
						sheet.addMergedRegion(new CellRangeAddress(sPos,ePos,colIndex,colIndex));
					}
				}
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	private void setMergeIdx(Map<String , List<Map<String,Object>>> mergeMap ,List<Map<String,Object>> datas , 
			String pColName , String colName , int groupIdx) {
		
		//그룹핑할 부모 인뎃스값
		int parentIdx = groupIdx -1;
		String preVal = null;//mergeMap의 list는 column에 대해 몇번 머지할ㅈ에 대한 정보임.
		
		if(pColName == null) 
		{//그룹 첫번째  merge 처리
			int rowPos = 0;
			for(int i = 0 ; i < datas.size(); i++) 
			{
				Map<String,Object> dataMap = objToMap(datas.get(i));
				String val = objToString(dataMap.get(colName));
				if(preVal == null) 
				{//merge start position
					preVal = val;
					List<Map<String, Object>> grpList = mergeMap.get(colName);
					if(grpList == null) {
						grpList = new ArrayList<>();
					}
					Map<String, Object> tmpMap = new HashMap<>();
					tmpMap.put("s_pos", i);
					tmpMap.put("s_data", val);
					tmpMap.put("column", colName);
					grpList.add(tmpMap);
					mergeMap.put(colName, grpList);
					
					Map<String,Object> endDataMap =null;
					if(datas.size() > i+1) {
						endDataMap = objToMap(datas.get(i+1));
					}
					if(endDataMap == null) {
						tmpMap.put("e_pos", i);
						preVal = null;
					}
					else {
						if(!val.equals(objToString(endDataMap.get(colName)))) 
						{//현재행과 다음행 비교 : 값이 틀리면 넣음
							tmpMap.put("e_pos", i);
							preVal = null;
						}
					}
				}
				else 
				{//preVal != null
					Map<String,Object> endDataMap =null;
					if(datas.size() > i+1) {
						endDataMap = objToMap(datas.get(i+1));//현재 위치의 다음 행 데이터를 봐야해서 +1
					}
					
					if(endDataMap == null) 
					{//해당 조건이 만족한다는것은 같은 데이터가 N개인 경우 마지막 데이터라서 e_pos넣어주고 반복문 빠져나온다
						List<Map<String, Object>> grpList= mergeMap.get(colName);
						Map<String, Object> tmpMap = grpList.get(grpList.size()-1);
						tmpMap.put("e_pos", i);
						tmpMap.put("e_data", preVal);
						preVal = null;
						continue;
					}
					
					//마지막 데이터가 아닐경우 이후 값을 가져와서 비교후 end data 넣어준다
					String endNextVal = objToString(endDataMap.get(colName));
					if(!preVal.equals(endNextVal)) {
						List<Map<String, Object>> grpList= mergeMap.get(colName);
						Map<String, Object> tmpMap = grpList.get(grpList.size()-1);
						tmpMap.put("e_pos", i);
						tmpMap.put("e_data", preVal);
						preVal = null;
					}
				}
			}
			
		}
		else 
		{//pColName != null   : 부모컬럼이 있을때 merge 처리
			
			List<Map<String, Object>> pMergeList= mergeMap.get(pColName);
			int pCurChildRowPos = 0;
			for(Map<String, Object> pMergeMap :pMergeList ) 
			{
				int p_s_pos = objToInt(pMergeMap.get("s_pos"));
				int p_e_pos = objToInt(pMergeMap.get("e_pos")) == -1 ? p_s_pos : objToInt(pMergeMap.get("e_pos"));
				String preChildVal = null;
				
				List<Map<String, Object>> childGrpList= mergeMap.get(colName);
				
				//최초진입 처리 부분
				if(childGrpList == null || childGrpList.size() == 0) 
				{
					childGrpList = new ArrayList<>();
					mergeMap.put(colName,  childGrpList);
				}
				else
				{
					
				}
				
				for(int j = p_s_pos ; j < p_e_pos; j++) 
				{
					Map<String, Object> dataMap = objToMap(datas.get(j));
					String val = objToString(dataMap.get(colName));
					if(preChildVal == null) 
					{
						preChildVal = val;
						Map<String, Object> childMap = new HashMap<>();
						childMap.put("s_pos", j);
						childMap.put("s_data", val);
						if(p_s_pos == p_e_pos) {
							childMap.put("e_pos", j);
							preChildVal = null;
						}
						childGrpList.add(childMap);
						
						//다음행 검사
						Map<String, Object> childNextDataMap = null;
						if(datas.size() >j+1) {
							childNextDataMap = objToMap(datas.get(j+1));
						}
						if(childNextDataMap == null) {
							childMap.put("e_pos", j);
							preChildVal = null;
						}
						else {
							if(!val.equals(objToString(childNextDataMap.get(colName)))) 
							{//현재행과 다음행 비교 : 값 틀리면 젛음
								childMap.put("e_pos", j);
								preChildVal = null;
							}
						}
					}
					else 
					{//preChildVal != null
						Map<String, Object> childMap = childGrpList.get(childGrpList.size()-1);
						Map<String, Object> childNextDataMap = null;
						if(datas.size() > j+1) {
							childNextDataMap = objToMap(datas.get(j+1));
						}
						if(childNextDataMap == null) {
							childMap.put("e_pos", j);
							preChildVal = null;
						}else {
							if(!val.equals(objToString(childNextDataMap.get(colName)))) 
							{//현재행과 다음행 비교 : 값 틀리면 젛음
								childMap.put("e_pos", j);
								preChildVal = null;
							}
						}
					}
					
				}
			}
			pCurChildRowPos++;
		}
		
	}
	
	//본문 셀 스타일 적용
	private void bodyCellStyle(Cell cell , Map<String,Object> header , Map<String,Object> colInfo) {
		String colName = objToString(header.get("column"));
		XSSFCellStyle preStyle = cellStyleContainer.get(colName);
		XSSFCellStyle style = null;
		
		if(preStyle == null) {
			style =  (XSSFCellStyle)workbook.createCellStyle();
			cellStyleContainer.put(colName, style);
		}else {
			cell.setCellStyle(preStyle);
			return;
		}
		
		XSSFWorkbook workbook = (XSSFWorkbook)cell.getSheet().getWorkbook();
		Font font = workbook.createFont();
		
		String colType = objToString(header.get("excel_col_type"));
		boolean isModify = objToBool(header.get("excel_modify"));
		String dataFormat = objToString(header.get("excel_format"));
		String align = objToString(header.get("excel_align"));
		
		font.setFontName(bodyFont);
		font.setColor(IndexedColors.BLACK.getIndex());
		style.setFont(font);
		
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);
		style.setBorderBottom(BorderStyle.THIN);
		
		if(!colType.isEmpty() && colType.toUpperCase().equals("STRING")) {
			DataFormat format = workbook.createDataFormat();
			style.setDataFormat(format.getFormat("@"));
		}
		else if(!colType.isEmpty() && !dataFormat.isEmpty()) {
			DataFormat format = workbook.createDataFormat();
			style.setDataFormat(format.getFormat(dataFormat));
		}
		else if(colType.toUpperCase().equals("DATE") && dataFormat.isEmpty()) {
			DataFormat format = workbook.createDataFormat();
			style.setDataFormat(format.getFormat("yyyy-MM-dd"));
		}else {
			
		}
		
		if(!align.isEmpty()) {
			if(align.toUpperCase().equals("LEFT"))        style.setAlignment(HorizontalAlignment.LEFT);
			else if(align.toUpperCase().equals("RIGHT"))  style.setAlignment(HorizontalAlignment.RIGHT);
			else if(align.toUpperCase().equals("CENTER")) style.setAlignment(HorizontalAlignment.CENTER);
			else                                          style.setAlignment(HorizontalAlignment.LEFT);
		}
		else {
			style.setAlignment(HorizontalAlignment.LEFT);
		}
		
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		
		if(isModify) {
			style.setLocked(false);
		}else {
			style.setLocked(true);
			XSSFColor enableEditCellColor = new XSSFColor(
				    new java.awt.Color(240, 240, 240),
				    new DefaultIndexedColorMap()
				);
			style.setFillForegroundColor(enableEditCellColor);
			style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		}
		
		//개행 
		style.setWrapText(true);
		cell.setCellStyle(style);
	}
	
	//1119라인
	public void createComboToSheet(XSSFSheet sheet , int startRowIdx , int endRowIdx , int colIndex ,
			List<Map<String,Object>> comboList , Map<String,Object> header) {
		try {
			boolean isModify = objToBool(header.get("excel_modify"));
			String [] items = new String[comboList.size()];
			
			if(items.length <= 0) return;
			
			int idx = 0;
			for(Map<String,Object>  item :comboList) {
				items[idx] = objToString(item.get("text")); idx++;
			}
			DataValidationConstraint constraint = sheet.getDataValidationHelper().createExplicitListConstraint(items);
			CellRangeAddressList addressList = new CellRangeAddressList(startRowIdx,endRowIdx,colIndex,colIndex);
			DataValidation dataValidation = sheet.getDataValidationHelper().createValidation(constraint, addressList);
			if(!isModify) {
				dataValidation.setSuppressDropDownArrow(false);
				dataValidation.createErrorBox("주의", "셀 값 수정 불가");
			}else {
				dataValidation.setSuppressDropDownArrow(true);
				dataValidation.createErrorBox("주의", "셀 값 수정 불가. 선택만 가능합니다.");
			}
			
			dataValidation.setShowErrorBox(true);
			dataValidation.setErrorStyle(DataValidation.ErrorStyle.STOP);
			sheet.addValidationData(dataValidation);
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	/** 콤보박스 생성 히든시트로 콤보 데이터 셋팅  */
	public void createComboToHiddenSheet(XSSFSheet sheet ,String colName , int startRowIdx , int endRowIdx , int colIndex ,
			List<Map<String,Object>> comboList , Map<String,Object> header) {
		try {
			boolean isModify = objToBool(header.get("excel_modify"));
			String [] items = new String[comboList.size()];
			
			if(items.length <= 0) return;
			
			XSSFSheet hiddenSheet = workbook.createSheet(colName);
			
			int idx = 0;
			for(Map<String,Object>  item :comboList) {
				items[idx] = objToString(item.get("text")); idx++;
			}
			
			for (int i=0 ; i < items.length ; i++) {
				Row row = hiddenSheet.createRow(i);
				Cell cell = row.createCell(0);
				cell.setCellValue(items[i]);
			}
			//시트 숨김
			workbook.setSheetHidden(workbook.getSheetIndex(hiddenSheet), true);
			//이름 지정
			Name nameRange = workbook.createName();
			nameRange.setNameName(colName+"_comboItems");
			nameRange.setRefersToFormula(colName+"!$A$1:$A$"+items.length);
			//유효성 설정
			DataValidationHelper helper = sheet.getDataValidationHelper();
			
			DataValidationConstraint constraint = helper.createFormulaListConstraint(colName+"_comboItems");
			CellRangeAddressList addressList = new CellRangeAddressList(startRowIdx,endRowIdx,colIndex,colIndex);
			DataValidation dataValidation = sheet.getDataValidationHelper().createValidation(constraint, addressList);
			if(!isModify) {
				dataValidation.setSuppressDropDownArrow(false);
				dataValidation.createErrorBox("주의", "셀 값 수정 불가");
			}else {
				dataValidation.setSuppressDropDownArrow(true);
				dataValidation.createErrorBox("주의", "셀 값 수정 불가. 선택만 가능합니다.");
			}
			
			dataValidation.setShowErrorBox(true);
			dataValidation.setErrorStyle(DataValidation.ErrorStyle.STOP);
			sheet.addValidationData(dataValidation);
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	/** cell 입력 제한 생성 */
	private void createValidationToCell(XSSFSheet sheet  , int startRowIdx , int endRowIdx , int colIndex ,
			 Map<String,Object> header) {
		try {
			String type   = objToString(header.get("excel_col_type"));
			String format = objToString(header.get("excel_format"));
			String vld    = objToString(header.get("excel_vld"));
			String msg    = objToString(header.get("excel_vld_err_msg"));
			if(type.isEmpty()) return;
			
			DataValidationHelper helper = sheet.getDataValidationHelper();
			DataValidationConstraint constraint =  null;
			String errorMsg = "";
			if(type.equals("number")) {
				constraint = helper.createNumericConstraint(DataValidationConstraint.ValidationType.INTEGER, 
						OperatorType.GREATER_OR_EQUAL, "0", null);
				errorMsg = msg.equals("") ? "숫자만 입력가능(소수점 불가능)":msg;
			}
			else if(type.equals("decimal")) {
				String cellPos = objToString(header.get("excel_col_pos"))+(startRowIdx+1);
				if(cellPos.length() < 2) {
					log.error("createValidationToCell param error column={} " , header.get("column"));
					return;
				}
				
				String customFomular = "";
				if(vld.equals("decimalPoint1")) 
				{//소수점 2째짜리 재한
					customFomular = "MOD(INDIRECT(ADDRESS(ROW(),COLUMN())) *10,1)=0";
					errorMsg = msg.equals("")?"소수 1자리까지 입력가능.":msg;
				}
				else if(vld.equals("decimalPoint2")) 
				{//소수점 2째짜리 재한
					customFomular = "MOD(INDIRECT(ADDRESS(ROW(),COLUMN())) *100,1)=0";
					errorMsg = msg.equals("")?"소수 2자리까지 입력가능.":msg;
				}
				else {
					if(!vld.equals("")) {
						customFomular = vld;
					}else {
						customFomular = "ISNUMBER("+cellPos+")";
						errorMsg = msg.equals("")?"숫자만 입력가능(소수 가능)":msg;
					}
				}
				
				//validation message 처리
				if(errorMsg.isEmpty() && msg.isEmpty()) {
					errorMsg = "입력이 잘못되었습니다";
				}else if(errorMsg.isEmpty() && !msg.isEmpty()) {
					errorMsg = msg;
				}
				
				
			}
			else {
				return;
			}
			
			CellRangeAddressList addressList = new CellRangeAddressList(startRowIdx,endRowIdx,colIndex,colIndex);
			DataValidation dataValidation = sheet.getDataValidationHelper().createValidation(constraint, addressList);
			
			dataValidation.setShowErrorBox(true);
			dataValidation.createErrorBox("입력오류", errorMsg);
			dataValidation.setErrorStyle(DataValidation.ErrorStyle.STOP);
			sheet.addValidationData(dataValidation);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/** comment 생성 */
	private void createCommentToCell(Cell cell , String commentData) {
		try {
			//주석 객체 생성
			CreationHelper factory = cell.getSheet().getWorkbook().getCreationHelper();
			Drawing<?> drawing = cell.getSheet().createDrawingPatriarch();
			ClientAnchor anchor =factory.createClientAnchor();
			
			//주석 위치 설정
			anchor.setCol1(cell.getColumnIndex());
			anchor.setCol2(cell.getColumnIndex() + 3);
			
			//텍스트의 길이에 따라 주석의 크기를 동적으로 설정
			int textLength = commentData.length();
			int rowCount = 5;//예상줄수
			anchor.setRow1(cell.getRowIndex());
			anchor.setRow2(cell.getRowIndex() + rowCount) ;
			
			//주석 생ㄹ성
			Comment comment = drawing.createCellComment(anchor);
			RichTextString str = factory.createRichTextString(commentData);
			comment.setString(str);
			
			cell.setCellComment(comment);
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	/** header 셀 스타일적용 (예:날짜 , 숫자 등) */
	private void headerCellStyle(Cell cell , String type , Map<String,Object> options) {
		
		try {
			XSSFWorkbook workbook = (XSSFWorkbook) cell.getSheet().getWorkbook();
			XSSFCellStyle style   = (XSSFCellStyle) workbook.createCellStyle();
			Font font = workbook.createFont();
			
			boolean isBold = objToBool(options.get("header_bold"));
			
			if(type.equals("confidential")) {
				font.setFontName(headerFont);
				font.setBold(isBold);
				font.setColor(IndexedColors.RED1.getIndex());
				style.setFont(font);
				style.setVerticalAlignment(VerticalAlignment.CENTER);
				style.setAlignment(HorizontalAlignment.RIGHT);
			}
			else if(type.equals("title")) {
				font.setFontName(headerFont);
				font.setFontHeight((short)(16*20));
				font.setBold(isBold);
				font.setColor(IndexedColors.BLACK1.getIndex());
				style.setFont(font);
				style.setVerticalAlignment(VerticalAlignment.CENTER);
			}
			else if(type.equals("header")) 
			{///헤더정보 스타일 셋팅
				
				
				String [] rgbStrs = objToString(options.get("header_color")).split(",");
				if(rgbStrs.length == 3) {
					
					try {
						int [] rgbs = new int[3];
						rgbs[0] = Integer.parseInt(rgbStrs[0]);
						rgbs[1] = Integer.parseInt(rgbStrs[1]);
						rgbs[2] = Integer.parseInt(rgbStrs[2]);
						
						XSSFColor enableEditCellColor = new XSSFColor(new java.awt.Color(255, 255, 255),new DefaultIndexedColorMap());
						style.setFillForegroundColor(enableEditCellColor);
						style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
					} catch (Exception e) {
						// TODO: handle exception
						XSSFColor enableEditCellColor = new XSSFColor(new java.awt.Color(255, 255, 255),new DefaultIndexedColorMap());
						style.setFillForegroundColor(enableEditCellColor);
						style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
					}
					
				}else {
					XSSFColor enableEditCellColor = new XSSFColor(new java.awt.Color(255, 255, 255),new DefaultIndexedColorMap());
					style.setFillForegroundColor(enableEditCellColor);
					style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				}
				
				style.setAlignment(HorizontalAlignment.CENTER);
				style.setVerticalAlignment(VerticalAlignment.CENTER);
				
				style.setBorderLeft(BorderStyle.THIN);
				style.setBorderRight(BorderStyle.THIN);
				style.setBorderTop(BorderStyle.THIN);
				style.setBorderBottom(BorderStyle.THIN);
				
				font.setFontName(headerFont);
//				font.setFontHeight((short)(16*20));
				font.setBold(isBold);
				font.setColor(IndexedColors.BLACK1.getIndex());
				style.setFont(font);
			}
			
			cell.setCellStyle(style);
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	
	
	private String objToString(Object obj) {
		if(obj == null) return "";
		if(obj instanceof String) return (String) obj;
		
		return "";
	}

	private Boolean objToBool(Object obj) {
		if(obj == null) return false;
		if(obj instanceof Boolean) return (Boolean) obj;
		
		return false;
	}

	private Integer objToInt(Object obj) {
		if(obj == null) return -1;
		if(obj instanceof Integer) return (Integer) obj;
		
		return -1;
	}

	private List objToList(Object obj) {
		if(obj == null) return new ArrayList<>();
		if(obj instanceof List) return (List) obj;
		
		return new ArrayList<>();
	}

	private Map objToMap(Object obj) {
		if(obj == null) return new HashMap<>();
		if(obj instanceof Map) return (Map) obj;
		
		return new HashMap<>();
	}
	
	
}
