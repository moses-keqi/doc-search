<template>
<div>
    <el-form :inline="true" style="margin-bottom: 10px;" >

        <el-button type="primary" @click="queryImportRecord">查找导入记录</el-button>
        <el-button type="primary" @click="importFromEvernote">从印象笔记导入</el-button>

    </el-form>
    <el-dialog
        title="选择文件"
        :visible.sync="dialogVisible"
        width="30%"
        >
        <input  ref="input" type="file" accept="application/enex" @change="importEvernote($event)">
        <el-button type="primary" size="small" @click="importEvernoteAjax">确定上传</el-button>
        <h5>从印象笔记导出笔记文件，然后上传，注意文件格式是enex，下面是操作示例图</h5>
        <img width="250" src="static/img/export-evernote.png"/>
        <span slot="footer" class="dialog-footer">
            <el-button @click="dialogVisible = false">取 消</el-button>
        </span>
    </el-dialog>
    <div >
        <el-table
            border
            :data="importList"
            style="width: 100%">
            <el-table-column
                prop="importDate"
                label="导入时间"
                width="200">
            </el-table-column>
            <el-table-column
                prop="importStatusText"
                label="导入状态"
                width="180">
            </el-table-column>
            <el-table-column
                prop="successSize"
                label="成功条数"
                width="140">
            </el-table-column>
            <el-table-column
                prop="importResult"
                label="导入结果"
                min-width="180">
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
</div>
</template>

<script>
    export default {
        name: 'evernote',
        created:function(){
            this.queryImportRecord();
        },
        data: function(){
            return {
                page:{
                    current: 1,
                    total:0,
                    size:10
                },
                dialogVisible:false,
                importList:[],
                evernoteFile:null
            }
        },
        methods: {
            queryImportRecord:function(){
                this.page.current  = 1;
                this.queryImportRecordAjax();
            },
            importFromEvernote:function(){
                this.dialogVisible=true;
                if(this.$refs.input){
                    this.$refs.input.value =''
                    this.evernoteFile = null;
                }
            },
            importEvernoteAjax:function(){
                var self=  this;
                if(self.evernoteFile==null){
                    self.$message.error('请选择印象笔记文件');
                    return;
                }
                var formData = new FormData();
                formData.append('file', self.evernoteFile);
                const loading = this.$loading({
                    lock: true,
                    text: '正在上传印象笔记',
                    spinner: 'el-icon-loading',
                    background: 'rgba(0, 0, 0, 0.7)'
                });
                self.$jq.ajax({
                    url: "/doc-search/doc/importEverNote",
                    type: 'POST',
                    cache: false,
                    data: formData,
                    processData: false,
                    contentType: false,
                    dataType:"json",
                    success:function (result,status) {
                        loading.close();
                        if(result.code===1){
                            self.$message({
                                message: '导入成功',
                                type: 'success'
                            });
                            self.dialogVisible=false;
                        }else {
                            self.$message.error(result.message);
                        }
                    },
                    fail: function (err, status) {
                        loading.close();
                        self.$message.error("网络错误");
                    },
                    // xhr: function(){
                    //     var xhr = self.$jq.ajaxSettings.xhr();
                    //     if(xhr.upload) {
                    //         xhr.upload.addEventListener("progress" , self.onUploadProgress, false);
                    //         return xhr;
                    //     }
                    // }
                })
            },
            importEvernote:function(event){
                var self = this;
                var files = event.target.files;
                var file = files[0];
                console.log(self.$utils.getSuffix(file.name));
                if(self.$utils.getSuffix(file.name)!=='enex'){
                    this.$refs.input.value =''
                    self.$message.error('请选择正确的印象笔记enez文件');
                    return;
                }
                self.evernoteFile =file;

            },
            onUploadProgress:function(evt){

            },
            queryImportRecordAjax:function () {
                var self = this;
                var params = {};
                params.current = self.page.current;
                params.size = self.page.size;
                self.$jq.post('/doc-search/doc/queryEverNoteImportRecord',params,function (result) {
                    if(result.code === 1){
                        self.page.current=result.data.page.current;
                        self.page.total=result.data.page.total;
                        var importList=result.data.list;
                        importList.forEach(function (doc,index) {
                            ////导入状态,0表示等待导入，1表示导入中，2表示导入成功，3表示导入失败
                            if(doc.importStatus===0){
                                doc.importStatusText = "等待导入";
                            }
                            if(doc.importStatus===1){
                                doc.importStatusText = "导入中";
                            }
                            if(doc.importStatus===2){
                                doc.importStatusText = "导入成功";
                            }
                            if(doc.importStatus===3){
                                doc.importStatusText = "导入失败";
                            }
                        })
                        self.importList =importList;
                    }else {
                        self.$message.error(result.message);
                    }
                },'json')
            },
            goto:function(current){
                this.page.current=current;
                this.queryImportRecordAjax();
            },
            sizeChange:function (size) {
                this.page.size=size;
                this.queryImportRecordAjax();
            }
        }
    }
</script>

<style scoped>

</style>
