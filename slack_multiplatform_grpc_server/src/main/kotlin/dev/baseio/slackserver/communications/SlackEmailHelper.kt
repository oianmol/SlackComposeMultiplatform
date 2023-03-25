package dev.baseio.slackserver.communications

import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage


object SlackEmailHelper {
    fun sendEmail(toEmail: String,link:String) {
        // Get a Properties object
        val properties: Properties = System.getProperties()
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.starttls.required", "true");
        properties.put("mail.smtp.ssl.protocols", "TLSv1.2");

        val username = System.getenv("EMAIL_FROM")
        val password = System.getenv("EMAIL_PASSWORD")
        val session: Session = Session.getDefaultInstance(properties, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(username, password)
            }
        })

        // -- Create a new message --
        val msg: Message = MimeMessage(session)

        // -- Set the FROM and TO fields --
        msg.setFrom(InternetAddress(System.getenv("EMAIL_USERNAME")))
        msg.setRecipients(
            Message.RecipientType.TO,
            InternetAddress.parse(toEmail, false)
        )
        msg.subject = "Confirm your email address on SlackClone"
        msg.setContent(emailTemplate(toEmail,link).also { println(it) }, "text/html")
        msg.sentDate = Date()
        Transport.send(msg)
    }
}

fun emailTemplate(toEmail: String, link: String) = "<html>\n" +
        "\n" +
        "<body>\n" +
        "    <div>\n" +
        "        <table>\n" +
        "            <td style=\"width:546px;vertical-align:top;padding-top:32px\">\n" +
        "                <div style=\"max-width:600px;margin:0 auto\">\n" +
        "                    <div style=\"margin-left:50px;margin-right:50px;margin-bottom:72px\"\n" +
        "                        class=\"m_-250469770731302257lg_margin_left_right m_-250469770731302257xl_margin_bottom\">\n" +
        "                        <div style=\"margin-top:18px\" class=\"m_-250469770731302257slack_logo_style\"><img width=\"120\"\n" +
        "                                height=\"36\" style=\"margin-top:0;margin-right:0;margin-bottom:32px;margin-left:0px\"\n" +
        "                                src=\"https://ci5.googleusercontent.com/proxy/6EtF2EzjH6vF6IRmC4-BEbNHd-9nZs7Kno_O8QSm_gvWZIBmhB0JpRI4SJ7Sd665k0G5gmCTpWbKetsJvnjNl9LeDWLymcQsaVmt9Q=s0-d-e1-ft#https://slack.com/x-a4255710924822/img/slack_logo_240.png\"\n" +
        "                                alt=\"slack logo\" class=\"CToWUd\" data-bit=\"iit\"></div>\n" +
        "                        <h1><span class=\"il\">Confirm</span> <span class=\"il\">your</span> <span class=\"il\">email</span>\n" +
        "                            <span class=\"il\">address</span> to get started on <span class=\"il\">Slack</span></h1>\n" +
        "                        <p style=\"font-size:20px;line-height:28px;letter-spacing:-0.2px;margin-bottom:28px;word-break:break-word\"\n" +
        "                            class=\"m_-250469770731302257hero_paragraph\">Once you’ve confirmed that <strong><a\n" +
        "                                    href=\"mailto:$toEmail\"\n" +
        "                                    target=\"_blank\">$toEmail</a></strong> is <span\n" +
        "                                class=\"il\">your</span> <span class=\"il\">email</span> <span class=\"il\">address</span>,\n" +
        "                            we’ll help you find <span class=\"il\">your</span> <span class=\"il\">Slack</span> workspaces or\n" +
        "                            create a new one.</p>\n" +
        "                        <p style=\"font-size:16px;line-height:24px;letter-spacing:-0.2px;margin-bottom:28px\"\n" +
        "                            class=\"m_-250469770731302257content_paragraph\"><img data-emoji=\"\uD83D\uDCF1\" class=\"an1\" alt=\"\uD83D\uDCF1\"\n" +
        "                                aria-label=\"\uD83D\uDCF1\" src=\"https://fonts.gstatic.com/s/e/notoemoji/15.0/1f4f1/72.png\"\n" +
        "                                loading=\"lazy\"> <strong>From <span class=\"il\">your</span> mobile device</strong>, tap\n" +
        "                            the button below to <span class=\"il\">confirm</span>:</p>\n" +
        "                        <table style=\"width:100%\">\n" +
        "                            <tbody>\n" +
        "<a href=\"$link\">Click here!</a>"+
        "                                <tr style=\"width:100%\">\n" +
        "                                    <td style=\"width:100%\"><span\n" +
        "                                            style=\"display:inline-block;border-radius:4px;background-color:#611f69;width:100%;text-align:center\"\n" +
        "                                            class=\"m_-250469770731302257button_link_wrapper m_-250469770731302257plum\"><a\n" +
        "                                                class=\"m_-250469770731302257button_link m_-250469770731302257plum m_-250469770731302257restyle_button\"\n" +
        "                                                href=\"$link\"\n" +
        "                                                style=\"border-top:13px solid;border-bottom:13px solid;border-right:24px solid;border-left:24px solid;border-color:#611f69;border-radius:4px;background-color:#611f69;color:#ffffff;font-size:16px;line-height:18px;word-break:break-word;font-weight:bold;font-size:14px;border-top:20px solid;border-bottom:20px solid;border-color:#611f69;line-height:14px;letter-spacing:0.8px;text-transform:uppercase;box-sizing:border-box;width:100%;text-align:center;display:inline-block;text-align:center;font-weight:900;text-decoration:none!important\"\n" +
        "                                                target=\"_blank\"\n" +
        "                                                data-saferedirecturl=\"https://www.google.com/url?q=$link\"><span\n" +
        "                                                    class=\"il\">Confirm</span> <span class=\"il\">Email</span> <span\n" +
        "                                                    class=\"il\">Address</span></a></span></td>\n" +
        "                                </tr>\n" +
        "                            </tbody>\n" +
        "                        </table>\n" +
        "                        <p style=\"font-size:16px;line-height:24px;letter-spacing:-0.2px;margin-bottom:28px;margin-top:40px\"\n" +
        "                            class=\"m_-250469770731302257content_paragraph\">If you didn’t request this <span\n" +
        "                                class=\"il\">email</span>,\n" +
        "                            there’s nothing to worry about — you can safely ignore it.</p>\n" +
        "                    </div>\n" +
        "                </div>\n" +
        "            </td>\n" +
        "        </table>\n" +
        "    </div>\n" +
        "</body>\n" +
        "\n" +
        "</html>"