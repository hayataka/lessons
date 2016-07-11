# sa-struts ajax
sa-struts をmavenのアーキタイプから作成
その後、　ajax対応を行う

{"errors": {"プロパティ名１": ["エラーメッセージ１","エラーメッセージ１´",...],  
"プロパティ名２": ["エラーメッセージ２","エラーメッセージ２´",...]},
"messages"  : (上記同様）,
"responseData": 各項目による値。ただし、errorsがある場合はnullを通常とする}


errorsまたはresponseDataのどちらかはnull

システムエラー時は ajaxのfail側になる（ようにする）



