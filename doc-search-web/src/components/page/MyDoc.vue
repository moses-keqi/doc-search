<template>
<div>
    <el-form :inline="true" >
        <el-form-item label="文件名搜索">
            <el-input v-model="query.docName"></el-input>
        </el-form-item>
        <el-form-item label="文档类型">
            <el-select v-model="query.docType"  placeholder="请选择">
                <el-option label="所有类型" value="all"></el-option>
                <el-option label="pdf" value="pdf"></el-option>
                <el-option label="word文档" value="word"></el-option>
                <el-option label="ppt文档" value="powerpoint"></el-option>
                <el-option label="excel文档" value="excel"></el-option>
                <el-option label="evernote" value="evernote"></el-option>
                <el-option label="网页" value="html"></el-option>
            </el-select>
        </el-form-item>
        <el-button type="primary" @click="queryDoc">查找文档</el-button>
        <el-button type="primary" @click="deleteSelectDoc">批量删除</el-button>
    </el-form>
    <div>
    <div style="float: left;width: 75%;">
        <el-table
            border
            :data="docList"
            @selection-change="handleSelectionChange"
            style="width: 100%">
            <el-table-column
                type="selection"
                width="55">
            </el-table-column>
            <el-table-column
                prop="docName"
                label="文档名字"
                width="180">
            </el-table-column>
            <el-table-column
                prop="docType"
                label="文档类型"
                width="100">
            </el-table-column>
            <el-table-column
                prop="docSizeText"
                label="文档大小"
                width="100">
            </el-table-column>
            <el-table-column
                prop="docCreateDate"
                label="创建时间"
                width="180">
            </el-table-column>
            <el-table-column
                label="索引进度"
                prop="docIndexText"
                width="150">
            </el-table-column>
            <el-table-column
                label="文档转换状态"
                prop="docConvertText"
                width="150">

            </el-table-column>
            <el-table-column
                fixed="right"
                label="操作"
                width="180">
                <template slot-scope="scope">
                    <el-button type="text" size="small" @click="deleteDoc(scope.row)">
                        删除
                    </el-button>
                    <el-button :disabled="scope.row.docConvert!=1" type="text" size="small" @click="previewDoc(scope.row)">
                        预览
                    </el-button>
                    <el-button type="text" size="small" @click="downloadDoc(scope.row)">
                        下载
                    </el-button>

                    <el-button :style="{ display: scope.row.docConvert==1?'none':'' }" type="text" size="small" @click="convertDoc(scope.row)">
                        转换
                    </el-button>

                </template>
            </el-table-column>
        </el-table>
        <div style="width: 100%;text-align: center;margin-top: 10px;">
            <el-pagination
                @size-change="sizeChange"
                @current-change="goto"
                :current-page="page.current"
                :page-sizes="[5, 10, 15, 20]"
                :page-size="page.size"
                layout="total, sizes, prev, pager, next, jumper"
                :total="page.total">
            </el-pagination>
        </div>
    </div>
        <div style="float: right;width: 20%;">
            <ve-pie :data="chartData"></ve-pie>
        </div>
    </div>
</div>
</template>

<script>
    import VePie from 'v-charts/lib/pie.common'
    export default {
        components:{
            VePie
        },
        name: 'myDoc',
        created:function(){
            this.queryDoc();
        },
        data: function(){
            this.chartSettings = {
                dimension: '文档类型',
                metrics: '文档数量'
            }
            return {
                query:{
                    docName:'',
                    docType:'all'
                },
                page:{
                    current: 1,
                    total:0,
                    size:10
                },
                docList:[],
                selectDocList:[],
                chartData: {
                    columns: ['文档类型', '文档数量'],
                    rows: [
                        // { '日期': '1/1', '访问用户': 1393 },
                        // { '日期': '1/2', '访问用户': 3530 },
                        // { '日期': '1/3', '访问用户': 2923 },
                        // { '日期': '1/4', '访问用户': 1723 },
                        // { '日期': '1/5', '访问用户': 3792 },
                        // { '日期': '1/6', '访问用户': 4593 }
                    ]
                }
            }
        },
        methods: {
            queryDoc:function(){
                this.page.current  = 1;
                this.queryDocAjax();
                this.queryDocTypeNumber();
            },
            queryDocTypeNumber:function(){
                var self = this;
                self.$jq.get("/doc-search/doc/queryDocTypeNumber",function (result) {
                    if(result.code===1){
                        if(!result.data.htmlNumber){
                            result.data.htmlNumber=0;
                        }
                        if(!result.data.pdfNumber){
                            result.data.pdfNumber=0;
                        }
                        if(!result.data.wordNumber){
                            result.data.wordNumber=0;
                        }
                        if(!result.data.excelNumber){
                            result.data.excelNumber =0;
                        }
                        if(!result.data.pptNumber){
                            result.data.pptNumber = 0;
                        }

                        var otherNumber = result.data.totalNumber-result.data.pdfNumber
                        -result.data.wordNumber-result.data.excelNumber-result.data.pptNumber
                        -result.data.htmlNumber;
                        var rows = [
                            { '文档类型': 'pdf', '文档数量': result.data.pdfNumber},
                            { '文档类型': 'word', '文档数量': result.data.wordNumber },
                            { '文档类型': 'excel', '文档数量': result.data.excelNumber },
                            { '文档类型': 'ppt', '文档数量': result.data.pptNumber },
                            { '文档类型': 'html', '文档数量': result.data.htmlNumber },
                            { '文档类型': '其它', '文档数量': otherNumber}
                        ];
                        self.chartData.rows=rows;
                    }else {
                        self.$message.error(result.message);
                    }
                },'json')
            },
            queryDocAjax:function () {
                var self = this;
                var params = {};
                params.paramsJson = JSON.stringify(self.query);
                params.current = self.page.current;
                params.size = self.page.size;
                self.$jq.post('/doc-search/doc/findDoc',params,function (result) {
                    if(result.code === 1){
                        self.page.current=result.data.page.current;
                        self.page.total=result.data.page.total;
                        var docList=result.data.list;
                        docList.forEach(function (doc,index) {
                            doc.docSizeText = self.$utils.getSize(doc.docSize);
                            ////0表示等待转换，1表示转换成功，2表示转换失败
                            ////0表示等待索引，1表示索引成功，2表示索引成功
                            if(doc.docConvert===0){
                                doc.docConvertText = "等待转换";
                            }
                            if(doc.docConvert===1){
                                doc.docConvertText = "文档转换成功";
                            }
                            if(doc.docConvert===2){
                                doc.docConvertText = "文档转换失败";
                            }
                            if(doc.docStatus===0){
                                doc.docIndexText = "等待索引";
                            }
                            if(doc.docStatus===1){
                                doc.docIndexText = "索引成功";
                            }
                            if(doc.docStatus===2){
                                doc.docIndexText = "索引失败";
                            }
                        })
                        self.docList = docList;
                    }else {
                        self.$message.error(result.message);
                    }
                },'json')
            },
            deleteDoc:function (doc) {
                var self  = this;
                this.$confirm('此操作将删除该文档, 是否继续?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    var docIdArray = [];
                    docIdArray.push(doc.docId);
                    self.deleteDocAjax(docIdArray);
                }).catch(() => {

                });
            },
            deleteDocAjax:function(docIdArray){
                var self  = this;
                var params = {};
                const loading = this.$loading({
                    lock: true,
                    text: '正在删除文档',
                    spinner: 'el-icon-loading',
                    background: 'rgba(0, 0, 0, 0.7)'
                });
                params.docIdArray = JSON.stringify(docIdArray);
                self.$jq.post('/doc-search/doc/deleteDoc',params,function (result) {
                    loading.close();
                    if(result.code === 1){
                        self.queryDocAjax();
                        self.$message({
                            message: '文档删除成功',
                            type: 'success'
                        });
                    }else {
                        self.$message.error(result.message);
                    }
                },'json')
            },
            downloadDoc:function(doc){
                window.location = "/doc-search/doc/downloadDocFile?docId="+doc.docId;
            },
            previewDoc:function(doc){
                var self = this;
                var params={};
                params.docId = doc.docId;
                if(doc.docType == 'evernote'){
                    self.$jq.ajax({
                        url: "/doc-search/doc/everNoteContent",
                        type: 'POST',
                        cache: false,
                        async: false,
                        data: params,
                        dataType:"json",
                        success:function (result,status) {
                            if(result.code===1){
                                const previewDialog= open("预览", "预览","status=no,menubar=yes,toolbar=no");
                                previewDialog.document.open();
                                previewDialog.document.write(result.data.docContent);
                                previewDialog.document.close();
                            }else {
                                self.$message.error(result.message);
                            }
                        },
                        fail: function (err, status) {
                            self.$message.error("网络错误");
                        }
                    })

                }else {
                    var pdfUrl = "/doc-search/doc/pdfFile?docId="+doc.docId;
                    pdfUrl = encodeURIComponent(pdfUrl);
                    window.open("static/pdf/viewer.html?pdfUrl="+pdfUrl);
                }

            },
            deleteSelectDoc:function(){
                var self  = this;
                this.$confirm('此操作将删除选择的所有文档, 是否继续?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    var docIdArray = [];
                    self.selectDocList.forEach(function (doc,index) {
                        docIdArray.push(doc.docId);
                    })
                    self.deleteDocAjax(docIdArray);
                }).catch(() => {

                });
            },
            convertDoc:function (doc) {
                var self  = this;
                var paramsData={};
                paramsData.docId = doc.docId;
                this.$confirm('此操作将转换该文档, 是否继续?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    self.$jq.ajax({
                        url: "/doc-search/doc/convertDoc",
                        type: 'POST',
                        cache: false,
                        async: false,
                        data: paramsData,
                        dataType:"json",
                        success:function (result,status) {
                            if(result.code===1){
                                self.$message.success("转换成功");
                                self.queryDoc()
                            }else {
                                self.$message.error(result.message);
                            }
                        },
                        fail: function (err, status) {
                            self.$message.error("网络错误");
                        }
                    })
                }).catch(() => {

                });
            },
            goto:function(current){
                this.page.current=current;
                this.queryDocAjax();
            },
            sizeChange:function (size) {
                this.page.size=size;
                this.queryDocAjax();
            },
            handleSelectionChange:function (val) {
                this.selectDocList=val;
            }
        }
    }
</script>

<style scoped>

</style>
