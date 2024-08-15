<template>
    <div class="login-wrap">
        <div class="ms-title">文档搜索</div>
        <div class="ms-login">
            <el-form :model="login" :rules="rules" ref="login" label-width="0px" class="demo-ruleForm">
                <el-form-item prop="username">
                    <el-input v-model="login.username" placeholder="username"></el-input>
                </el-form-item>
                <el-form-item prop="password">
                    <el-input type="password" placeholder="password" v-model="login.password" @keyup.enter.native="submitForm('login')"></el-input>
                </el-form-item>
                <div class="login-btn">
                    <el-button type="primary" :loading="loginLoading" @click="submitForm('login')">登录</el-button>
                </div>
            </el-form>
        </div>
    </div>
</template>

<script>
    export default {
        data: function(){
            return {
                loginLoading:false,
                login: {
                    username: 'admin',
                    password: 'yt520'
                },
                rules: {
                    username: [
                        { required: true, message: '请输入用户名', trigger: 'blur' }
                    ],
                    password: [
                        { required: true, message: '请输入密码', trigger: 'blur' }
                    ]
                }
            }
        },
        methods: {
            submitForm(formName) {
                var self  = this;
                this.$refs[formName].validate((valid) => {
                    if (valid) {
                        self.loginAjax(self.login.username,self.login.password);
                    } else {
                        console.log('error submit!!');
                        return false;
                    }
                });
            },
            loginAjax:function (name,pwd) {
                var self  = this;
                var params = {};
                params.name = name;
                params.pwd = pwd;
                self.loginLoading = true;
                self.$jq.post("/doc-search/usr/login",params,function (result) {
                    self.loginLoading = false;
                    if(result.code === 1){
                        localStorage.setItem('ms_username',name);
                        self.$router.push('/');
                    }else {
                        self.$message.error(result.message);
                    }
                })
            }
        }
    }
</script>

<style scoped>
    .login-wrap{
        position: relative;
        width:100%;
        height:100%;
    }
    .ms-title{
        position: absolute;
        top:50%;
        width:100%;
        margin-top: -230px;
        text-align: center;
        font-size:30px;
        color: #fff;

    }
    .ms-login{
        position: absolute;
        left:50%;
        top:50%;
        width:300px;
        height:160px;
        margin:-150px 0 0 -190px;
        padding:40px;
        border-radius: 5px;
        background: #fff;
    }
    .login-btn{
        text-align: center;
    }
    .login-btn button{
        width:100%;
        height:36px;
    }
</style>
